#!/bin/sh
# Setup a Vagrant VM for Ontrack

# Help function
function show_help {
	echo "Ontrack vagrant environment."
	echo ""
	echo "Available options are:"
	echo "General:"
	echo "    -h, --help                    Displays this help"
	echo "Vagrant:"
	echo "    -v, --vagrant                 Vagrant command (defaults to 'vagrant')"
	echo "    -s, --vagrant-source          Directory that contains the Vagrant definition (defaults to 'vagrant')"
	echo "    -l, --vagrant-local           Directory that will contain the Vagrant working directory (defaults to 'vagrant-local')"
	echo "    -p, --preserve                If set to 'true', the local environment is kept (default to 'false')"
	echo "Ontrack:"
	echo "    -j, --jar                     (* required) Path to the Ontrack JAR"
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

VAGRANT=vagrant
VAGRANT_SOURCE=vagrant
VAGRANT_LOCAL=vagrant-local

PRESERVE=false

ONTRACK_JAR=

# Command central

for i in "$@"
do
	case $i in
		-h|--help)
			show_help
			exit 0
			;;
		-v=*|--vagrant=*)
			VAGRANT=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-s=*|--vagrant-source=*)
			VAGRANT_SOURCE=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-l=*|--vagrant-local=*)
			VAGRANT_LOCAL=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-p=*|--preserve=*)
			PRESERVE=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-j=*|--jar=*)
            ONTRACK_JAR=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		*)
			echo "Unknown option: $i"
			show_help
			exit 1
		;;
	esac
done

# Checking

check "$ONTRACK_JAR" "Ontrack JAR (--jar) is required."

# Logging

echo "Vagrant command   = ${VAGRANT}"
echo "Vagrant sources   = ${VAGRANT_SOURCE}"
echo "Vagrant local wd  = ${VAGRANT_LOCAL}"
echo "Preserve local wd = ${PRESERVE}"
echo "Ontrack JAR       = ${ONTRACK_JAR}"

# Setup the vagrant environment locally

echo "Preparing local environment in ${VAGRANT_LOCAL}"
if [ "${PRESERVE}" != "true" ]
then
    echo Copying Vagrant files locally in ${VAGRANT_LOCAL}
    rm -rf ${VAGRANT_LOCAL}
    mkdir -p ${VAGRANT_LOCAL}
    cp -r ${VAGRANT_SOURCE}/* ${VAGRANT_LOCAL}
fi

# Preparing the source JAR

echo Copying the Ontrack JAR at the correct location
mkdir -p ${VAGRANT_LOCAL}/sources
rm -rf ${VAGRANT_LOCAL}/sources/*.jar
cp ${ONTRACK_JAR} ${VAGRANT_LOCAL}/sources/ontrack.jar

# Launching Vagrant

echo Creating the VM
cd ${VAGRANT_LOCAL}
${VAGRANT} up
