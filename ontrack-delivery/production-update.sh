#!/bin/bash

# Upgrade of a machine with a version of Ontrack
#
# This file is to be uploaded and executed on the machine to upgrade.

# Help function
function show_help {
	echo "Ontrack production deployment script."
	echo ""
	echo "Available options are:"
	echo "    -h, --help                    Displays this help"
	echo "    -v, --version=<version>       Version to deploy (required)"
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

VERSION=

# Command central

for i in "$@"
do
	case $i in
		-h|--help)
			show_help
			exit 0
			;;
		-v=*|--version=*)
			VERSION=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		*)
			echo "Unknown option: $i"
			show_help
			exit 1
		;;
	esac
done

# Checking

check "${VERSION}" "Version (--version) is required."

# Logging

echo "[PRODUCTION] Version         = ${VERSION}"

# Removes the Nginx container

NGINX_CID=`docker ps | grep "dockerfile/nginx" | awk "{print \$1}"`
docker stop ${NGINX_CID}
docker rm ${NGINX_CID}

# Stops the old version of Ontrack

docker stop $(docker ps docker ps | grep "nemerosa/ontrack" | awk "{print \$1}")

# Backup of the data

TIMESTAMP=`date +%F-%T`
docker run --volumes-from ontrack-data --volume /root:/backup ubuntu tar czvf /backup/backup-${TIMESTAMP}.tgz /opt/ontrack/mount

# Installs the new version of Ontrack

docker run -d --name ontrack-${VERSION} --volumes-from ontrack-data nemerosa/ontrack:${VERSION}

# Recreate the Nginx container:

docker run -d \
    --publish 443:443 \
    --link ontrack-${VERSION}:ontrack \
    --volume /root/nginx/certs:/etc/nginx/certs \
    --volume /root/nginx/sites-enabled:/etc/nginx/sites-enabled \
    dockerfile/nginx

# End