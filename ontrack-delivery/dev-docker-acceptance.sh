#!/bin/bash

# Runs the acceptance tests in a VM created using Docker
# The results of the acceptance tests will be in JUnit XML format in local `ontrack-acceptance.xml` file
# See `docker-acceptance.sh` for the argument details

# Before being run, the application must have been built:
#
#    # Development mode
#    ./gradlew build
#
# or
#
#    # Release mode
#    ./gradlew release

# Root of the project
PROJECT=`dirname $0`/..

# Getting the UI jar
UI=`find ${PROJECT}/ontrack-ui/build/libs -name "*.jar"`
if [ "$?" != "0" ]
then
	echo "Could not find the UI JAR"
	exit 1
fi
echo "Found UI JAR at $UI"

# Getting the Acceptance jar
ACCEPTANCE=`find ${PROJECT}/ontrack-acceptance/build/libs -name "*.jar"`
if [ "$?" != "0" ]
then
	echo "Could not find the Acceptance JAR"
	exit 1
fi
echo "Found Acceptance JAR at $ACCEPTANCE"

# Running the docker acceptance tests
cd ${PROJECT}/ontrack-delivery
./docker-acceptance.sh \
	--jar=${PROJECT}/${UI} \
	--acceptance=${PROJECT}/${ACCEPTANCE} \
	$*

