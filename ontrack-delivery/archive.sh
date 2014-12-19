#!/bin/bash

# Script to archive and prepare artifacts need for acceptance tests and deployment
#
# Sample usage
#
# ontrack-delivery/archive.sh --source=${WORKSPACE} --destination=~/repository/ontrack/2.0 --move

# Help function
function show_help {
	echo "Ontrack archiving script."
	echo ""
	echo "Available options are:"
	echo "    -h, --help               Displays this help"
	echo "    -s, --source=<dir>       Directory that contains the sources and artifacts, defaults to current directory"
	echo "    -d, --destination=<dir>  Directory that will contain the artifacts, defaults to 'dist'"
	echo "    -m, --move               If set, moves the artifacts instead of copying them"
}

# Defaults

SOURCE=`pwd`
DESTINATION=`pwd`/dist
MV=cp

# Command central

for i in "$@"
do
	case $i in
		-h|--help)
			show_help
			exit 0
			;;
		-s=*|--source=*)
			SOURCE=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-d=*|--destination=*)
            DESTINATION=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-m|--move)
            MV=mv
			;;
		*)
			echo "Unknown option: $i"
			show_help
			exit 1
		;;
	esac
done

# Logging

echo "Source      = ${SOURCE}"
echo "Destination = ${DESTINATION}"
echo "Mode        = ${MV}"

# Destination directory

echo "Creating ${DESTINATION} directory"
mkdir -p ${DESTINATION}

# Copy of artifacts

echo -n "Copy of artefacts"

echo -n .
${MV} ontrack-ui/build/libs/ontrack-ui-*.jar ${DESTINATION}
echo -n .
${MV} ontrack-acceptance/build/libs/ontrack-acceptance-*.jar ${DESTINATION}
echo -n .
${MV} ontrack-delivery/build/ontrack-delivery-docker-*.jar ${DESTINATION}
echo -n .
${MV} ontrack-common/build/libs/ontrack-common-*.jar ${DESTINATION}
echo -n .
${MV} ontrack-json/build/libs/ontrack-json-*.jar ${DESTINATION}
echo -n .
${MV} ontrack-client/build/libs/ontrack-client-*.jar ${DESTINATION}
echo -n .
${MV} ontrack-dsl/build/libs/ontrack-dsl-*.jar ${DESTINATION}
echo -n .
${MV} ontrack-common/build/poms/ontrack-common-*.pom ${DESTINATION}
echo -n .
${MV} ontrack-json/build/poms/ontrack-json-*.pom ${DESTINATION}
echo -n .
${MV} ontrack-client/build/poms/ontrack-client-*.pom ${DESTINATION}
echo -n .
${MV} ontrack-dsl/build/poms/ontrack-dsl-*.pom ${DESTINATION}

echo
echo "Artifacts have been copied"
