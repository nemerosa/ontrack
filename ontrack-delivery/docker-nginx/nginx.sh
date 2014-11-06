#!/bin/bash

# Help function
function show_help {
	echo "Nginx configuration generation script."
	echo ""
	echo "Available options are:"
	echo "    -h, --help                    Displays this help"
	echo "Generation options:"
	echo "    -t, --target=<dir>            Target directory for the generated file (defaults to 'build')"
	echo "    -f, --file=<file>             Name of the generated file (defaults to 'nginx.conf')"
	echo "Configuration options:"
	echo "    -h, --host=<host>             Host of the target (defaults to '127.0.0.1')"
	echo "    -p, --port=<port>             Port on the target (defaults to '8080')"
	echo "    -n, --name=<name>             Server name (defaults to 'untitled')"
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

TARGET=build
FILE=nginx.conf
HOST=127.0.0.1
PORT=8080
NAME=untitled

# Command central

for i in "$@"
do
	case $i in
		-h|--help)
			show_help
			exit 0
			;;
		-t=*|--target=*)
			TARGET=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-f=*|--file=*)
			FILE=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-h=*|--host=*)
			HOST=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-p=*|--port=*)
			PORT=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-n=*|--name=*)
			NAME=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		*)
			echo "Unknown option: $i"
			show_help
			exit 1
		;;
	esac
done

# Checking

# check "$ACCEPTANCE_JAR" "Acceptance JAR (--jar) is required."

# Logging

echo "Target directory      = ${TARGET}"
echo "Target file           = ${FILE}"
echo "Target host           = ${HOST}"
echo "Target port           = ${PORT}"
echo "Server name           = ${NAME}"

# Environment preparation

mkdir -p ${TARGET}

# Generation

cat << EOF > ${TARGET}/${FILE}
upstream app_server {
    server ${HOST}:${PORT} fail_timeout=0;
}

server {
    listen 443 ssl;
    server_name ${NAME};

    # ssl on;
    ssl_certificate /etc/nginx/ssl/server.pem;
    ssl_certificate_key /etc/nginx/ssl/server.key;

    error_log /var/log/nginx/nginx.vhost.error.log;

    location / {
        proxy_set_header Host \$http_host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-Host ${NAME};
        proxy_set_header X-Forwarded-Port 443;
        proxy_set_header X-Forwarded-Proto https;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_redirect http:// https://;

        add_header Pragma "no-cache";

        proxy_pass http://app_server;

    }
}
EOF
