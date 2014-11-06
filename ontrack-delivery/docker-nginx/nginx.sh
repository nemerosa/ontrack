#!/bin/bash

# Help function
function show_help {
	echo "Nginx configuration generation script."
	echo ""
	echo "Available options are:"
	echo "    -h, --help                    Displays this help"
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

#echo "Ontrack URL      = ${ONTRACK_URL}"
#echo "Ontrack context  = ${ONTRACK_CONTEXT}"
#echo "Acceptance JAR   = ${ACCEPTANCE_JAR}"

# Environment preparation

mkdir -p build

# Generation

cat << EOF > build/nginx.conf
upstream app_server {
    # TODO Configurable target port
    server 127.0.0.1:8080 fail_timeout=0;
}

server {
    listen 443 ssl;
    # TODO Configurable server name
    server_name ontrack.nemerosa.net;

    # ssl on;
    # TODO Configurable certificates
    ssl_certificate /etc/nginx/ssl/ontrack_nemerosa_net.pem;
    ssl_certificate_key /etc/nginx/ssl/ontrack_nemerosa_net.key;

    error_log /var/log/nginx/nginx.vhost.error.log;

    location / {
        proxy_set_header Host $http_host;
        proxy_set_header X-Real-IP $remote_addr;
        # TODO Configurable server name
        proxy_set_header X-Forwarded-Host ontrack.nemerosa.net;
        proxy_set_header X-Forwarded-Port 443;
        proxy_set_header X-Forwarded-Proto https;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_redirect http:// https://;

        add_header Pragma "no-cache";

        proxy_pass http://app_server;

    }
}
EOF
