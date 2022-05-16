#!/bin/bash

set -euxo pipefail

HOSTNAME=`hostname`

  OPTS=`getopt -o h: --long hostname: -n 'parse-options' -- "$@"`
  if [ $? != 0 ] ; then echo "Failed parsing options." >&2 ; exit 1 ; fi

  echo "$OPTS"
  eval set -- "$OPTS"

  while true; do
    case "$1" in
      -h | --hostname )     HOSTNAME=$2;        shift; shift ;;
      -- ) shift; break ;;
      * ) break ;;
    esac
  done
echo "Using HOSTNAME='$HOSTNAME'"

until mongo --eval "print(\"waited for connection\")"
  do
    echo "Wait for connection"
    sleep .5
  done

mongo localhost:27017/kogito <<-EOF
    rs.initiate({
        _id: "rs0",
        members: [ { _id: 0, host: "${HOSTNAME}:27017" } ]
    });
EOF

echo "Initiated replica set"

mongo localhost:27017/admin <<-EOF
    db.createUser({ user: 'admin', pwd: 'admin', roles: [ { role: "userAdminAnyDatabase", db: "admin" } ] });
EOF

mongo -u admin -p admin localhost:27017/admin <<-EOF
    db.runCommand({
        createRole: "listDatabases",
        privileges: [
            { resource: { cluster : true }, actions: ["listDatabases"]}
        ],
        roles: []
    });

    db.runCommand({
        createRole: "readChangeStream",
        privileges: [
            { resource: { db: "", collection: ""}, actions: [ "find", "changeStream" ] }
        ],
        roles: []
    });

    db.createUser({
        user: "$MONGODB_USER",
        pwd: "$MONGODB_PASSWORD",
        roles: [
            { role: "readWrite", db: "kogito" },
            { role: "read", db: "local" },
            { role: "listDatabases", db: "admin" },
            { role: "readChangeStream", db: "admin" },
            { role: "read", db: "config" },
            { role: "read", db: "admin" }
        ]
    });
EOF
echo "MongoDB initialized"