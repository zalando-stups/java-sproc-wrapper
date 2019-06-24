#!/bin/bash

if ! which docker &> /dev/null ; then
    echo 'Ensure that docker is installed and is in PATH'
    exit 1
fi

if ! docker info | grep -q 'Server Version' ; then
    echo 'Ensure that docker service is running'
    exit 1
fi

if nc -w 5 -z localhost 5432; then
    echo 'There is already some process listening on port 5432.'
    echo 'Please shutdown any existing PostgreSQL instance and re-run this script.'
    exit 1
fi

PGVERSION=9.6.10

container=$(docker ps | grep postgres:$PGVERSION)
if [ -z "$container" ]; then
    echo 'Starting PostgreSQL instance..'
    docker-compose up -d
fi

until nc -w 5 -z localhost 5432; do
    echo 'Waiting for Postgres port..'
    sleep 3
done

sleep 5

echo 'Running tests..'
# Uncomment if need to run tests with debug
#export MAVEN_OPTS="-Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"
./mvnw clean verify -Pintegration-test

echo 'Stopping PostgreSQL instance..'
docker-compose down
