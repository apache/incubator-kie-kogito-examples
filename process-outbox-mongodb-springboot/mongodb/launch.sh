#!/bin/bash

set -m

bash -c '/usr/local/bin/docker-entrypoint.sh mongod --replSet rs0 --auth --keyFile /usr/local/bin/keyfile' &

bash -c '/usr/local/bin/init.sh'

fg %1