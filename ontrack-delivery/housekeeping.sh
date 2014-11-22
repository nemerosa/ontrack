#!/bin/bash

# Housekeeping procedures linked to the cleanup of resources created by the
# pipeline of Ontrack.
#
# This script must be run from Jenkins itself.

# TODO Unreleased images in Docker Hub
# TODO Unreleased images in Jenkins
# TODO Stopped containers at Ontrack @ Ontrack
# TODO Obsolete acceptance images in Digital Ocean

# Help function
function show_help {
	echo "Ontrack housekeeping script."
	echo ""
	echo "Available options are:"
	echo "    -h, --help                    Displays this help"
	echo "    --dry-run                     Disable actual clean-up, just traces"
	echo "    -do, --digital-ocean          Digital Ocean housekeeping"
	echo "Digital Ocean specific options are:"
	echo "    -dot, --do-token=<token>      Personal Access Token (required)"
	echo "    -dop, --do-pattern=<pattern>  Regex to use to identify droplets to clean (default: 'ontrack-acceptance.*')"
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

DO_TOKEN=
DO_PATTERN="ontrack-acceptance.*"

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

# Logging

echo "Dry run                = ${DRYRUN}"
echo "Digital Ocean          = ${DIGITAL_OCEAN}"
echo "Digital Ocean pattern  = ${DO_PATTERN}"

# Housekeeping procedures

function digital_ocean {
    echo "Digital Ocean housekeeping"
    ./housekeeping-digitalocean.py \
        --token "${DO_TOKEN}" \
        --pattern "${DO_PATTERN}"
}
# Housekeeping script

if [ "${DIGITAL_OCEAN}" == "yes" ]
then
    digital_ocean
fi
