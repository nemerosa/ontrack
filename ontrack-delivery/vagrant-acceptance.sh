#!/bin/sh
# Runs the acceptance tests in a VM created using Vagrant

# Help function
function show_help {
	echo "Ontrack vagrant environment."
	echo ""
	echo "Available options are:"
	echo "General:"
	echo "    -h, --help                    Displays this help"
	echo "Ontrack to test:"
	echo "    -u, --ontrack-url             URL to test (defaults to http://localhost:8080)"
	echo "    -p, --ontrack-password        Password for the administrator (defaults to 'admin')"
	echo "    -c, --ontrack-context         Text context (defaults to 'default')"
	echo "Test setup:"
	echo "    -j, --jar                     (* required) Path to the acceptance test JAR"
}

# Check function

function check {
	if [ "$1" == "" ]
	then
		echo $2
		exit 1
	fi
}

# Environment

# Source of Vagrant configuration files
SRC=vagrant
# Target for the local Vagrant environment
WD=vagrant-local

# Setup the vagrant environment locally

echo Copying Vagrant files locally in ${WD}
rm -rf ${WD}
mkdir -p ${WD}
cp -r ${SRC}/* ${WD}

# Launching Vagrant

echo Creating the VM
cd ${WD}
vagrant up

# Destroying the VM
echo Destroying the VM
vagrant destroy -f
