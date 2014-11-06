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
	echo "    --dry-run                     Does not run the acceptance tests"
	echo "Control:"
	echo "    -k, --keep                    If set, the container is not destroyed after"
	echo "    -d, --delay                   Number of seconds to wait for Ontrack to start (defaults to 120)"
	echo "    -u, --docker-user=<user>      Docker user UID or name to use (defaults to none, can be computed using 'id -u <user>')"
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
CONTROL_USER=
CONTROL_DRY_RUN=no

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
		--dry-run)
            CONTROL_DRY_RUN=yes
			;;
		-u=*|--docker-user=*)
            CONTROL_USER=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
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
echo "Docker user:            ${CONTROL_USER}"
echo "Ontrack protocol:       ${ONTRACK_PROTOCOL}"
echo "Ontrack JAR:            ${ONTRACK_JAR}"
echo "Startup delay:          ${CONTROL_DELAY} s"
echo "Keeping containers:     ${CONTROL_KEEP}"
echo "Dry run:                ${CONTROL_DRY_RUN}"

# Mount point

MOUNT=`pwd`/acceptance
rm -f ${MOUNT}
mkdir -p ${MOUNT}
echo "Ontrack data at:        ${MOUNT}"

# Docker Ontrack VM up
# TODO Docker nginx VM up

DOCKER_OPTIONS=
if [ "${CONTROL_USER}" != "" ]
then
    DOCKER_OPTIONS="${DOCKER_OPTIONS} --docker-user=${CONTROL_USER}"
fi
echo "Docker options:         ${DOCKER_OPTIONS}"

# Ontrack container

echo "Starting the Ontrack container"
# TODO Do not expose Ontrack port
./docker-setup.sh \
    --docker-image=ontrack \
    --mount=${MOUNT} \
    ${DOCKER_OPTIONS} \
    --run \
    --jar=${ONTRACK_JAR}

ONTRACK_CID=`cat ontrack.cid`
ONTRACK_NAME=`docker inspect -f "{{ .Name }}" ${ONTRACK_CID}`
ONTRACK_NAME="${ONTRACK_NAME:1}"

echo "[ACCEPTANCE] Ontrack container created: ${ONTRACK_CID} (${ONTRACK_NAME})"

# nginx container

echo "[ACCEPTANCE] Preparation of the nginx image"

# Generation of the nginx configuration and generation of self-signed certificates
docker-nginx/nginx.sh \
	--target=`pwd`/docker-nginx/build \
	--host=ontrack \
	--port=8080 \
	--name=ontrack \
	--cert-subject="/C=BE/L=Brussel/CN=ontrack"


# Generation of the Nginx image
echo "[ACCEPTANCE] Building the nginx image..."
docker build -t="ontrack-nginx" docker-nginx/

# Creating the Nginx container
echo "[ACCEPTANCE] Starting the nginx container..."
docker run -d -P  --cidfile=nginx.cid ontrack-nginx
NGINX_CID=`cat nginx.cid`
echo "[ACCEPTANCE] Nginx container created: ${NGINX_CID}"

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
ONTRACK_STARTED=no
ONTRACK_START_DURATION=0
for i in `seq 1 ${CONTROL_DELAY}`
do
    curl --silent --fail "${ONTRACK_URL}/info"
    if [ "$?" != "0" ]
    then
        echo -n "."
        sleep 1
    else
        ONTRACK_STARTED=yes
        if [ "${ONTRACK_START_DURATION}" == "0" ]
        then
            ONTRACK_START_DURATION=${i}
        fi
    fi
done
echo

# Has Ontrack started correctly?
if [ "${ONTRACK_STARTED}" == "yes" ]
then

    echo "[ACCEPTANCE] Ontrack has started in ${ONTRACK_START_DURATION} s"

    # Dry run
	if [ "${CONTROL_DRY_RUN}" == "yes" ]
	then
		echo "[ACCEPTANCE] DRYRUN - not running the acceptance tests."
		ACCEPTANCE_RESULT=0
	else
		# Running the acceptance tests
		echo "[ACCEPTANCE] Starting acceptance tests..."
		./acceptance.sh \
			--ontrack-url=${ONTRACK_URL} \
			--jar=${ONTRACK_ACCEPTANCE_JAR}
		# Result of the acceptance tests
		ACCEPTANCE_RESULT=$?
		echo "[ACCEPTANCE] Results: ${ACCEPTANCE_RESULT}"
    fi

else
    ACCEPTANCE_RESULT=1
    echo "[ACCEPTANCE] Ontrack could not start in less than ${CONTROL_DELAY} s."
fi

# Docker Ontrack VM down

if [ "${CONTROL_KEEP}" == "no" ]
then
    echo "[ACCEPTANCE] Removing Ontrack container at: ${ONTRACK_CID}"
    docker rm -f ${ONTRACK_CID}
    echo "[ACCEPTANCE] Removing Nginx container at: ${NGINX_CID}"
    docker rm -f ${NGINX_CID}
fi

# Result
exit ${ACCEPTANCE_RESULT}
