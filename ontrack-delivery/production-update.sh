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
	echo "    -d, --delay                   Number of seconds to wait for Ontrack to start (defaults to 300, 0 means no wait)"
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
ONTRACK_DELAY=300

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
		-d=*|--delay=*)
            ONTRACK_DELAY=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
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
echo "[PRODUCTION] Startup delay   = ${ONTRACK_DELAY}"

# Removes the Nginx container

echo "[PRODUCTION] Stopping Nginx container..."
NGINX_CID=`docker ps | grep "dockerfile/nginx" | awk '{print $1}'`
if [ "${NGINX_CID}" != "" ]
then
	docker stop ${NGINX_CID}
	docker rm ${NGINX_CID}
fi

# Stops the old version of Ontrack

echo "[PRODUCTION] Stopping Ontrack container..."
ONTRACK_CID=`docker ps docker ps | grep "nemerosa/ontrack" | awk '{print $1}'`
if [ "${ONTRACK_CID}" != "" ]
then
	docker stop ${ONTRACK_CID}
fi

# Backup of the data

echo "[PRODUCTION] Backuping Ontrack data..."
TIMESTAMP=`date +%F-%T`
docker run --volumes-from ontrack-data --volume /root:/backup ubuntu tar czvf /backup/backup-${TIMESTAMP}.tgz --exclude='work/files/git/*' /opt/ontrack/mount

# Installs the new version of Ontrack

echo "[PRODUCTION] Starting new Ontrack version ${VERSION}..."
docker run -d --name ontrack-${VERSION} --volumes-from ontrack-data nemerosa/ontrack:${VERSION}

# Recreate the Nginx container:

echo "[PRODUCTION] Starting the Nginx proxy container..."
docker run -d \
    --publish 443:443 \
    --link ontrack-${VERSION}:ontrack \
    --volume /root/nginx/certs:/etc/nginx/certs \
    --volume /root/nginx/sites-enabled:/etc/nginx/sites-enabled \
    dockerfile/nginx

# Waiting for the application to start

echo "[PRODUCTION] Waiting for Ontrack to start (max: ${ONTRACK_DELAY} s)"
ONTRACK_STARTED=no
ONTRACK_START_DURATION=0
for i in `seq 1 ${ONTRACK_DELAY}`
do
    curl --silent --fail --insecure "https://localhost/info"
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

if [ "${ONTRACK_STARTED}" != "yes" ]
then
	echo "[PRODUCTION] Ontrack could not start is less than ${ONTRACK_DELAY}s"
	exit 1
fi

# End
echo "[PRODUCTION] End of deployment."
