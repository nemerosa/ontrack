#!/bin/bash

# Help function
function show_help {
	echo "Ontrack acceptance test script."
	echo ""
	echo "Available options are:"
	echo "General:"
	echo "    -h, --help                    Displays this help"
	echo "Ontrack to test:"
	echo "    -u, --ontrack-url             URL to test (defaults to http://localhost:8080)"
	echo "    -p, --ontrack-password        Password for the administrator (defaults to 'admin')"
	echo "    -c, --ontrack-context         Text context (defaults to 'default')"
	echo "    -d, --delay                   Number of seconds to wait for Ontrack to start (defaults to 120, 0 means no wait)"
	echo "    --dry-run                     Does not run the acceptance tests"
	echo "Test setup:"
	echo "    -j, --jar=<file>              (* required) Path to the acceptance test JAR"
	echo "    -ns, --no-ssl                 Set to disable SSL checks (use only for acceptance testing)"
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

ONTRACK_URL=http://localhost:8080
ONTRACK_PASSWORD=admin
ONTRACK_CONTEXT=default
ONTRACK_DELAY=120
ONTRACK_DRY_RUN=no

ACCEPTANCE_JAR=
ACCEPTANCE_DISABLE_SSL=no

# Command central

for i in "$@"
do
	case $i in
		-h|--help)
			show_help
			exit 0
			;;
		-u=*|--ontrack-url=*)
			ONTRACK_URL=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-p=*|--ontrack-password=*)
            ONTRACK_PASSWORD=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-c=*|--ontrack-context=*)
            ONTRACK_CONTEXT=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-d=*|--delay=*)
            ONTRACK_DELAY=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		--dry-run)
            ONTRACK_DRY_RUN=yes
			;;
		-j=*|--jar=*)
            ACCEPTANCE_JAR=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-ns|--no-ssl)
            ACCEPTANCE_DISABLE_SSL=yes
			;;
		*)
			echo "Unknown option: $i"
			show_help
			exit 1
		;;
	esac
done

# Checking

check "$ACCEPTANCE_JAR" "Acceptance JAR (--jar) is required."

# Logging

echo "[ACCEPTANCE] Ontrack URL      = ${ONTRACK_URL}"
echo "[ACCEPTANCE] Ontrack context  = ${ONTRACK_CONTEXT}"
echo "[ACCEPTANCE] Acceptance JAR   = ${ACCEPTANCE_JAR}"
echo "[ACCEPTANCE] Disabling SSL    = ${ACCEPTANCE_DISABLE_SSL}"
echo "[ACCEPTANCE] Waiting delay    = ${ONTRACK_DELAY} s"
echo "[ACCEPTANCE] Dry run          = ${ONTRACK_DRY_RUN}"

# Waiting for the start

echo "[ACCEPTANCE] Waiting for Ontrack to start (max: ${ONTRACK_DELAY} s)"
ONTRACK_STARTED=no
ONTRACK_START_DURATION=0
for i in `seq 1 ${ONTRACK_DELAY}`
do
    curl --silent --fail --insecure "${ONTRACK_URL}/info"
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
        break
    fi
done
echo

# Execution of the tests
function run_tests {
	java -jar ${ACCEPTANCE_JAR} \
		--ontrack.url=${ONTRACK_URL} \
		--ontrack.admin=${ONTRACK_PASSWORD} \
		--ontrack.disableSSL=${ACCEPTANCE_DISABLE_SSL} \
		--ontrack.context=${ONTRACK_CONTEXT}
}


# Has Ontrack started correctly?
if [ "${ONTRACK_STARTED}" == "yes" ]
then

    echo "[ACCEPTANCE] Ontrack has started in ${ONTRACK_START_DURATION} s"

    # Dry run
	if [ "${ONTRACK_DRY_RUN}" == "yes" ]
	then
		echo "[ACCEPTANCE] DRYRUN - not running the acceptance tests."
		exit 0
	else
		# Running the acceptance tests
		echo "[ACCEPTANCE] Starting acceptance tests..."
		run_tests
		# Result of the acceptance tests
		ACCEPTANCE_RESULT=$?
		echo "[ACCEPTANCE] Results: ${ACCEPTANCE_RESULT}"
		if [ "${ACCEPTANCE_RESULT}" == "0" ]
		then
			echo "[ACCEPTANCE] Acceptance tests were OK."
			exit 0
		else
			echo "[ACCEPTANCE] Acceptance tests have FAILED!"
			exit 1
		fi
    fi

else
    echo "[ACCEPTANCE] Ontrack could not start in less than ${ONTRACK_DELAY} s."
    exit 1
fi