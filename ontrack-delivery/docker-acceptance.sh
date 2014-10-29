#!/bin/sh
# Runs the acceptance tests in a VM created using Docker

# Help function
function show_help {
	echo "Ontrack acceptance tests using Docker."
	echo ""
	echo "Available options are:"
	echo "General:"
	echo "    -h, --help                    Displays this help"
	echo "Input:"
	echo "    -j, --jar                     (* required) Path to the ontrack JAR"
	echo "    -a, --acceptance              (* required) Path to the acceptance test JAR"
	echo "Control:"
	echo "    -k, --keep                    If set, the container is not destroyed after"
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

ONTRACK_JAR=

CONTROL_KEEP=no

# Command central

for i in "$@"
do
	case $i in
		-h|--help)
			show_help
			exit 0
			;;
		-j=*|--jar=*)
            ONTRACK_JAR=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-k|--keep)
            CONTROL_KEEP=yes
			;;
		*)
			echo "Unknown option: $i"
			show_help
			exit 1
		;;
	esac
done

# Checks

check "$ONTRACK_JAR" "Ontrack JAR (--jar) is required."

# Logging
echo "Ontrack JAR:            ${ONTRACK_JAR}"
echo "Keeping containers:     ${CONTROL_KEEP}"

# Mount point

MOUNT=`pwd`/acceptance
rm -f ${MOUNT}
mkdir -p ${MOUNT}
echo "Ontrack data at:        ${MOUNT}"

# Docker Ontrack VM up
# TODO Docker nginx VM up

./docker-setup.sh \
    --docker-image=ontrack \
    --mount=${MOUNT} \
    --run \
    --jar=${ONTRACK_JAR}

ONTRACK_CID=`cat ontrack.cid`

echo "[ACCEPTANCE] Ontrack container created: ${ONTRACK_CID}"

# TODO Get the running URL

# ONTRACK_URL="http://${DOCKER_HOST}"

# TODO Running the acceptance tests

# echo "[ACCEPTANCE] Running acceptance tests against ${ONTRACK_URL}"

# Docker Ontrack VM down
# TODO Docker nginx VM down

if [ "${CONTROL_KEEP}" == "no" ]
then
    echo "[ACCEPTANCE] Removing Ontrack container at: ${ONTRACK_CID}"
    docker rm -f ${ONTRACK_CID}
fi
