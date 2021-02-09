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

mongo localhost:27017/kogito <<-EOF
    rs.initiate({
        _id: "rs0",
        members: [ { _id: 0, host: "${HOSTNAME}:27017" } ]
    });
EOF
echo "Initiated replica set"

sleep 3
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

    db.createUser({
        user: 'debezium',
        pwd: 'dbz',
        roles: [
            { role: "readWrite", db: "kogito" },
            { role: "read", db: "local" },
            { role: "listDatabases", db: "admin" },
            { role: "read", db: "config" },
            { role: "read", db: "admin" }
        ]
    });
EOF

echo "Created users"

mongo -u debezium -p dbz --authenticationDatabase admin localhost:27017/kogito <<-EOF
    use kogito;

    db.test.insert([
        { _id : NumberLong("1"), name : 'one' },
        { _id : NumberLong("2"), name : 'two' },
        { _id : NumberLong("3"), name : 'three' },
        { _id : NumberLong("4"), name : 'four' },
        { _id : NumberLong("5"), name : 'five' },
        { _id : NumberLong("6"), name : 'six' },
        { _id : NumberLong("7"), name : 'seven' },
        { _id : NumberLong("8"), name : 'eight' },
        { _id : NumberLong("9"), name : 'nine' }
    ]);
EOF

echo "Inserted example data"
