#!/bin/sh
# Setup a Docker container for Ontrack

# Help function
function show_help {
	echo "Ontrack docker container."
	echo ""
	echo "Available options are:"
	echo "General:"
	echo "    -h, --help                   Displays this help"
	echo "Docker:"
	echo "    -s, --docker-source          Directory that contains the Docker definition (defaults to 'docker')"
	echo "    -i, --docker-image           Name of the Docker image to create (defaults to 'ontrack')"
	echo "    -v, --docker-version         Version of the Docker image to create (by default, computed from the JAR name)"
	echo "Ontrack:"
	echo "    -j, --jar                    (* required) Path to the Ontrack JAR"
}

# Check function

function check {
	if [ "$1" == "" ]
	then
		echo $2
		exit 1
	fi
}

# Defaults

DOCKER_SOURCE=docker
DOCKER_IMAGE=ontrack
DOCKER_VERSION=

ONTRACK_JAR=

# Command central

for i in "$@"
do
	case $i in
		-h|--help)
			show_help
			exit 0
			;;
		-s=*|--docker-source=*)
			DOCKER_SOURCE=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-i=*|--docker-image=*)
			DOCKER_IMAGE=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-v=*|--docker-version=*)
			DOCKER_VERSION=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-j=*|--jar=*)
            ONTRACK_JAR=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		*)
			echo "Unknown option: $i"
			show_help
			exit 1
		;;
	esac
done

# Checking

check "$ONTRACK_JAR" "Ontrack JAR (--jar) is required."

# Version computation from the JAR if not fixed
if [ "" == "${DOCKER_VERSION}" ]
then
    DOCKER_VERSION=`basename $ONTRACK_JAR | sed -E 's/ontrack(-ui)?-(.*)\.jar/\2/'`
fi

# Logging

echo "Docker source        = ${DOCKER_SOURCE}"
echo "Docker image name    = ${DOCKER_IMAGE}"
echo "Docker image version = ${DOCKER_VERSION}"
echo "Ontrack JAR          = ${ONTRACK_JAR}"

# Setup the Docker environment locally

mkdir -p ${DOCKER_SOURCE}/ontrack
cp ${ONTRACK_JAR} ${DOCKER_SOURCE}/ontrack/ontrack.jar

# Launching Docker

echo Building the Docker Ontrack image

docker build -t="${DOCKER_IMAGE}:${DOCKER_VERSION}" ${DOCKER_SOURCE}
