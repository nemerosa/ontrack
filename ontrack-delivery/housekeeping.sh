#!/bin/bash

# Housekeeping procedures linked to the cleanup of resources created by the
# pipeline of Ontrack.
#
# This script must be run from Jenkins itself.

# TODO Unreleased images in Docker Hub
# TODO Unreleased images in Jenkins
# TODO Stopped containers at Ontrack @ Ontrack

# Help function
function show_help {
	echo "Ontrack housekeeping script."
	echo ""
	echo "Available options are:"
	echo "    -h, --help                    Displays this help"
	echo "    --dry-run                     Disable actual clean-up, just traces"
	echo "    -do, --digital-ocean          Digital Ocean housekeeping"
	echo "    -br, --binary-repository      Binary repository housekeeping"
	echo "Digital Ocean specific options are:"
	echo "    -dot, --do-token=<token>      Personal Access Token (required)"
	echo "    -dop, --do-pattern=<pattern>  Regex to use to identify droplets to clean (default: 'ontrack-acceptance.*')"
	echo "Binary repository specific options are:"
	echo "    -brd, --br-dir=<dir>          Repository directory (required)"
	echo "    -brt, --br-time=<days>        Number of retention days (default: 30 days)"
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

DRYRUN=no

DIGITAL_OCEAN=no
BINARY_REPOSITORY=no

DO_TOKEN=
DO_PATTERN="ontrack-acceptance.*"

BINARY_REPOSITORY_DIR=
BINARY_REPOSITORY_DAYS=30

# Command central

for i in "$@"
do
	case $i in
		-h|--help)
			show_help
			exit 0
			;;
		--dry-run)
			DRYRUN=yes
			;;
		-do|--digital-ocean)
            DIGITAL_OCEAN=yes
			;;
		-dot=*|--do-token=*)
            DO_TOKEN=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-dop=*|--do-pattern=*)
            DO_PATTERN=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-br|--binary-repository)
            BINARY_REPOSITORY=yes
			;;
		-brd=*|--br-dir=*)
            BINARY_REPOSITORY_DIR=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-brt=*|--br-time=*)
            BINARY_REPOSITORY_DAYS=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		*)
			echo "Unknown option: $i"
			show_help
			exit 1
		;;
	esac
done

# Checking

if [ "${DIGITAL_OCEAN}" == "yes" ]
then
	check "${DO_TOKEN}" "Digital Ocean Personal Access Token (--do-token) is required."
fi

if [ "${BINARY_REPOSITORY}" == "yes" ]
then
	check "${BINARY_REPOSITORY_DIR}" "Binary repository directory (--br-dir) is required."
fi

# Logging

echo "Dry run                = ${DRYRUN}"
echo "---------"
echo "Digital Ocean          = ${DIGITAL_OCEAN}"
echo "Digital Ocean pattern  = ${DO_PATTERN}"
echo "---------"
echo "Binary repository      = ${BINARY_REPOSITORY}"
echo "Binary repository dir  = ${BINARY_REPOSITORY_DIR}"
echo "Binary repository days = ${BINARY_REPOSITORY_DAYS}"

# Housekeeping procedures

function digital_ocean {
    echo "Digital Ocean housekeeping"
    ./housekeeping-digitalocean.py \
        --token "${DO_TOKEN}" \
        --pattern "${DO_PATTERN}"
}

function binary_repository {
	echo "Binary repository housekeeping"
	find "${BINARY_REPOSITORY_DIR}" -ctime "+${BINARY_REPOSITORY_DAYS}" -exec rm -rf {} \;
}
# Housekeeping script

if [ "${DIGITAL_OCEAN}" == "yes" ]
then
    digital_ocean
fi


if [ "${BINARY_REPOSITORY}" == "yes" ]
then
    binary_repository
fi
