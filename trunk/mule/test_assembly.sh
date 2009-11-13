#!/bin/sh

PROJECT_HOME=$(dirname $0)
PROJECT_HOME=$( cd $PROJECT_HOME && pwd )

VERSION=1.0-SNAPSHOT

echo "Building assembly..."
mvn clean assembly:assembly
cd target

echo "Unpacking assembly..."
tar xvzf mule-$VERSION.tar.gz
echo "Preparing assembly executable..."
cd mule-$VERSION
chmod +x ./bin/mule.sh
echo "Starting test..."
./bin/mule.sh start