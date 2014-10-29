#!/bin/sh
# Setup a Docker container for Ontrack

# Help function
function show_help {
	echo "Ontrack docker container."
	echo ""
	echo "Available options are:"
	echo "General:"
	echo "    -h, --help                      Displays this help"
	echo "Docker:"
	echo "    -s, --docker-source=<dir>       Directory that contains the Docker definition (defaults to 'docker')"
	echo "    -i, --docker-image=<name>       Name of the Docker image to create (defaults to 'ontrack')"
	echo "    -v, --docker-version=<version>  Version of the Docker image to create (by default, computed from the JAR name)"
	echo "Ontrack:"
	echo "    -j, --jar=<path>                (* required) Path to the Ontrack JAR"
	echo "Control:"
	echo "    -r, --run                       If set, runs the container automatically"
	echo "                                    * the container ID will be written in the local 'ontrack.cid' file (see also --cid)"
	echo "    -m, --mount=<dir>               Directory where to store Ontrack persistent data for the run."
	echo "                                    * It defaults to <pwd>/mount where <pwd> is the local directory."
	echo "                                    * Note that this directory will be created automatically by Docker if it does not exist."
	echo "                                    * This directory is used only when running the container immediately (--run)."
	echo "    -c, --cid=<file>                Path to the file that will contain the contained ID (defaults to 'ontrack.cid')"
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
ONTRACK_MOUNT=`pwd`/mount

CONTROL_RUN=no
CONTROL_CID=ontrack.cid


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
		-m=*|--mount=*)
            ONTRACK_MOUNT=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-c=*|--cid=*)
            CONTROL_CID=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
	    -r|--run)
	        CONTROL_RUN=yes
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
echo "Ontrack mount        = ${ONTRACK_MOUNT}"
echo "Running auto         = ${CONTROL_RUN}"
echo "Container ID file    = ${CONTROL_CID}"

# Setup the Docker environment locally

mkdir -p ${DOCKER_SOURCE}/ontrack
cp ${ONTRACK_JAR} ${DOCKER_SOURCE}/ontrack/ontrack.jar

# Creating the Docker image

echo Building the Docker Ontrack image

docker build -t="${DOCKER_IMAGE}:${DOCKER_VERSION}" ${DOCKER_SOURCE}

# Running the Docker image immediately

if [ "${CONTROL_RUN}" == "yes" ]
then
    rm -f ${CONTROL_CID}
    docker run -d -P \
        -v ${ONTRACK_MOUNT}:/opt/ontrack/mount \
        --cidfile=${CONTROL_CID} \
        ${DOCKER_IMAGE}:${DOCKER_VERSION}
fi
