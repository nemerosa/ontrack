#!/bin/bash
# Runs the acceptance tests in a VM created using Docker
# The results of the acceptance tests will be in JUnit XML format in local `ontrack-acceptance.xml` file

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
	echo "    -h, --host                    Address of the Docker container (defaults to 'localhost' or IP in 'DOCKER_HOST')"
	echo "Control:"
	echo "    -k, --keep                    If set, the container is not destroyed after"
	echo "    -d, --delay                   Number of seconds to wait for Ontrack to start (defaults to 120)"
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

if [ "${DOCKER_HOST}" == "" ]
then
    ONTRACK_HOST=localhost
else
    ONTRACK_HOST=`echo ${DOCKER_HOST} | sed -E 's/.*\/([0-9\.]+):.*/\1/'`
fi

# TODO Supports https: through nginx
ONTRACK_PROTOCOL=http
ONTRACK_JAR=
ONTRACK_ACCEPTANCE_JAR=

CONTROL_KEEP=no
CONTROL_DELAY=120

# Command central

for i in "$@"
do
	case $i in
		-h|--help)
			show_help
			exit 0
			;;
		-h=*|--host=*)
            ONTRACK_HOST=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-j=*|--jar=*)
            ONTRACK_JAR=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-a=*|--acceptance=*)
            ONTRACK_ACCEPTANCE_JAR=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-d=*|--delay=*)
            CONTROL_DELAY=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
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
check "$ONTRACK_ACCEPTANCE_JAR" "Ontrack Acceptance JAR (--acceptance) is required."

# Logging
echo "Docker host:            ${ONTRACK_HOST}"
echo "Ontrack protocol:       ${ONTRACK_PROTOCOL}"
echo "Ontrack JAR:            ${ONTRACK_JAR}"
echo "Startup delay:          ${CONTROL_DELAY} s"
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

# Getting the public facing port

ONTRACK_PORT=`docker port ${ONTRACK_CID} 8080 | sed -E 's/.*:(.*)/\1/'`
echo "[ACCEPTANCE] Ontrack available in container at port ${ONTRACK_PORT}"

# Get the running URL

ONTRACK_URL="${ONTRACK_PROTOCOL}://${ONTRACK_HOST}:${ONTRACK_PORT}"
echo "[ACCEPTANCE] Running acceptance tests against ${ONTRACK_URL}"

# Result of the acceptance tests
ACCEPTANCE_RESULT=-1

# Waits until the application is started
echo -n "[ACCEPTANCE] Waiting for Ontrack to start (max: ${CONTROL_DELAY} s)"
ONSTART_STARTED=no
for i in `seq 1 ${CONTROL_DELAY}`
do
    curl --silent --fail "${ONTRACK_URL}/info"
    if [ "$?" != "0" ]
    then
        echo -n "."
        sleep 1
    else
        ONSTART_STARTED=yes
    fi
done
echo

# Has Ontrack started correctly?
if [ "${ONSTART_STARTED}" == "yes" ]
then

    # Running the acceptance tests
    ./acceptance.sh \
        --ontrack-url=${ONTRACK_URL} \
        --jar=${ONTRACK_ACCEPTANCE_JAR}

    # Result of the acceptance tests
    ACCEPTANCE_RESULT=$?
    echo "[ACCEPTANCE] Results: ${ACCEPTANCE_RESULT}"

else
    ACCEPTANCE_RESULT=1
    echo "[ACCEPTANCE] Ontrack could not start in less than ${CONTROL_DELAY} s."
fi

# Docker Ontrack VM down
# TODO Docker nginx VM down

if [ "${CONTROL_KEEP}" == "no" ]
then
    echo "[ACCEPTANCE] Removing Ontrack container at: ${ONTRACK_CID}"
    docker rm -f ${ONTRACK_CID}
fi

# Result
exit ${ACCEPTANCE_RESULT}
