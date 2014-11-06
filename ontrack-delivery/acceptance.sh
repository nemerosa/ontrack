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

echo "Ontrack URL      = ${ONTRACK_URL}"
echo "Ontrack context  = ${ONTRACK_CONTEXT}"
echo "Acceptance JAR   = ${ACCEPTANCE_JAR}"
echo "Disabling SSL    = ${ACCEPTANCE_DISABLE_SSL}"

# Execution of the tests

java -jar ${ACCEPTANCE_JAR} \
    --ontrack.url=${ONTRACK_URL} \
    --ontrack.admin=${ONTRACK_PASSWORD} \
    --ontrack.disableSSL=${ACCEPTANCE_DISABLE_SSL} \
    --ontrack.context=${ONTRACK_CONTEXT}
