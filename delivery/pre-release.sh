#!/bin/sh
# Script to execute BEFORE the release

# Build number must be provided
if [ "" == "$BUILD_NUMBER" ]
then
    echo BUILD_NUMBER must be provided.
    exit 1
fi

VERSION=`cat pom.xml | grep SNAPSHOT | sed -E 's/.*>([0-9\.A-Z]*)-SNAPSHOT.*/\1/'`
RELEASE=${VERSION}-${BUILD_NUMBER}

if [ "" == "$VERSION" ]
then
    echo Version could not be determined.
    exit 1
fi

echo Version      = ${VERSION}
echo Build number = ${BUILD_NUMBER}
echo Release      = ${RELEASE}
echo VERSION=${VERSION} > version.properties
echo RELEASE=${RELEASE} >> version.properties
echo ${RELEASE} > .release

echo Changing version in POM to ${RELEASE}
mvn versions:set --quiet -DnewVersion=${RELEASE} -DgenerateBackupPoms=false | grep -v Props
