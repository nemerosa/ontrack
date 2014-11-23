#!/bin/bash

# Installation of Ontrack using Vagrant

# Help function
function show_help {
	echo "Installation of Ontrack using Vagrant+Docker."
	echo ""
	echo "Available options are:"
	echo "    -h, --help                          Displays this help"
	echo "    -vg, --vagrant=<dir>                Directory that contains the Vagrant file (default: 'vagrant')"
	echo "    -vgh, --vagrant-host=<host>         Name to give to the VM (default: 'ontrack-vagrant')"
	echo "    -vgp, --vagrant-provider=<provider> Provider for Vagrant (default: 'virtualbox')"
	echo "    -k, --authorized-key=<file>         Path to a file that contains a public key to add to the authorized keys of the VM being created (default: none)"
	echo "Docker options are:"
	echo "    -i, --image=<image>                 Docker image to pull (default: 'nemerosa/ontrack:latest')"
	echo "Nginx options are:"
	echo "    -nc, --nginx-certs=<dir>            Directory where to get the Nginx certificates from (default: './nginx/certs')"
	echo "Digital Ocean specific options are:"
	echo "    -dot, --do-token=<token>            Personal Access Token (required)"
	echo "    -dor, --do-region=<region>          Region (required, for example: ams2 or nyc2)"
	echo "    -dos, --do-size=<size>              Droplet size (default: '512m')"
	echo "    -doi, --do-image=<image>            Droplet image that contains Docker (default: 'Docker 1.3.1 on 14.04')"
	echo "    -dok, --do-key=<name>               Name of the SSH Key in DO (default: 'Vagrant')"
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
VAGRANT_HOST="ontrack-vagrant"
VAGRANT_PROVIDER=virtualbox

AUTHORIZED_KEY=
DOCKER_IMAGE_ONTRACK="nemerosa/ontrack:latest"
NGINX_CERTS="./nginx/certs"

DO_TOKEN=
DO_REGION=
DO_SIZE=512mb
DO_IMAGE="Docker 1.3.1 on 14.04"
DO_KEY_NAME="Vagrant"

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
		-vgh=*|--vagrant-host=*)
            VAGRANT_HOST=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-vgp=*|--vagrant-provider=*)
            VAGRANT_PROVIDER=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-k=*|--authorized-key=*)
            AUTHORIZED_KEY=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-i=*|--image=*)
            DOCKER_IMAGE_ONTRACK=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-nc=*|--nginx-certs=*)
            NGINX_CERTS=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-dot=*|--do-token=*)
            DO_TOKEN=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-dor=*|--do-region=*)
            DO_REGION=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-dos=*|--do-size=*)
            DO_SIZE=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-doi=*|--do-image=*)
            DO_IMAGE=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-dok=*|--do-key=*)
            DO_KEY_NAME=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		*)
			echo "Unknown option: $i"
			show_help
			exit 1
		;;
	esac
done

# Checks

if [ "${VAGRANT_PROVIDER}" == "digital_ocean" ]
then
	check "${DO_TOKEN}" "Digital Ocean Personal Access Token (--do-token) is required."
	check "${DO_REGION}" "Digital Ocean region (--do-region) is required."
fi

# Logging

echo "Vagrant directory:     ${VAGRANT_DIR}"
echo "Vagrant VM host:       ${VAGRANT_HOST}"
echo "Vagrant provider:      ${VAGRANT_PROVIDER}"
echo "Docker Ontrack image:  ${DOCKER_IMAGE_ONTRACK}"
echo "Nginx certificates at: ${NGINX_CERTS}"
if [ "${VAGRANT_PROVIDER}" == "digital_ocean" ]
then
	echo "Digital Ocean Personal Access Token:   ***"
	echo "Digital Ocean region:                  ${DO_REGION}"
	echo "Digital Ocean size:                    ${DO_SIZE}"
	echo "Digital Ocean image:                   ${DO_IMAGE}"
	echo "Digital Ocean key name:                ${DO_KEY_NAME}"
fi

# Sets the vagrant environment

export VAGRANT_CWD=${VAGRANT_DIR}
rm -rf ${VAGRANT_DIR}/.vagrant

# Configuration of the Vagrant file

export VAGRANT_HOST
export DOCKER_IMAGE_ONTRACK
export NGINX_CERTS

if [ "${AUTHORIZED_KEY}" != "" ]
then
	export AUTHORIZED_KEY
fi

if [ "${VAGRANT_PROVIDER}" == "digital_ocean" ]
then
	export DO_TOKEN
	export DO_REGION
	export DO_SIZE
	export DO_IMAGE
	export DO_KEY_NAME
fi

# Creating the machine

vagrant up --provider ${VAGRANT_PROVIDER}
