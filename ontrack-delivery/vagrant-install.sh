#!/bin/bash

# Installation of Ontrack using Vagrant

# Help function
function show_help {
	echo "Installation of Ontrack using Vagrant+Docker."
	echo ""
	echo "Available options are:"
	echo "    -h, --help                    Displays this help"
	echo "    -vg, --vagrant=<dir>          Directory that contains the Vagrant file (default: 'vagrant')"
}

# Check function

function check {
	if [ "$1" == "" ]
	then
		echo $2
		exit 1
	fi
}

# Default values

VAGRANT_DIR=vagrant

# Command central

for i in "$@"
do
	case $i in
		-h|--help)
			show_help
			exit 0
			;;
		-vg=*|--vagrant=*)
            VAGRANT_DIR=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		*)
			echo "Unknown option: $i"
			show_help
			exit 1
		;;
	esac
done

# Logging

echo "Vagrant directory:   ${VAGRANT_DIR}"

# Sets the vagrant environment

export VAGRANT_CWD=${VAGRANT_DIR}

# Creating the machine

vagrant up

