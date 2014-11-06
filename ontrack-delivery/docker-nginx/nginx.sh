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
	echo "Certificates options:"
	echo "    -cp, --cert-prm=<pem>         Copies the PEM file (--copy-key must also be provided)"
	echo "    -ck, --cert-key=<key>         Copies the Key file (--copy-pem must also be provided)"
	echo "    -cg, --cert-generate          Generates the certificates (default)"
	echo "    -cs, --cert-subject           Subject of the certificate (defaults to /C=BE/L=Brussel/CN=www.example.com)"
}

# Defaults

TARGET=build
FILE=nginx.conf
HOST=127.0.0.1
PORT=8080
NAME=untitled
CERT_PEM=
CERT_KEY=
CERT_GENERATE=yes
CERT_SUBJECT=/C=BE/L=Brussel/CN=www.example.com

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
		-cp=*|--cert-pem=*)
			CERT_PEM=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-ck=*|--cert-key=*)
			CERT_KEY=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-cs=*|--cert-subject=*)
			CERT_SUBJECT=`echo $i | sed 's/[-a-zA-Z0-9]*=//'`
			;;
		-cg|--cert-generate)
			CERT_GENERATE=yes
			;;
		*)
			echo "Unknown option: $i"
			show_help
			exit 1
		;;
	esac
done

# Checking

if [ "${CERT_PEM}" != "" ]
then
	if [ "${CERT_KEY}" == "" ]
	then
		echo "--cert-key is required when --cert-pem is given."
		exit 1
	fi
	if [ ! -e "${CERT_PEM}" ]
	then
		echo "PEM file does not exist: ${CERT_PEM}"
		exit 1
	fi
	CERT_GENERATE=no
fi
if [ "${CERT_KEY}" != "" ]
then
	if [ "${CERT_PEM}" == "" ]
	then
		echo "--cert-pem is required when --cert-key is given."
		exit 1
	fi
	if [ ! -e "${CERT_KEY}" ]
	then
		echo "KEY file does not exist: ${CERT_KEY}"
		exit 1
	fi
	CERT_GENERATE=no
fi

# check "$ACCEPTANCE_JAR" "Acceptance JAR (--jar) is required."

# Logging

echo "Target directory       = ${TARGET}"
echo "Target file            = ${FILE}"
echo "Target host            = ${HOST}"
echo "Target port            = ${PORT}"
echo "Server name            = ${NAME}"
if [ "${CERT_GENERATE}" == "no" ]
then
	echo "Certificate PEM file   = ${CERT_PEM}"
	echo "Certificate KEY file   = ${CERT_KEY}"
else
	echo "Certificate generation = yes"
	echo "Certificate subject    = ${CERT_SUBJECT}"
fi

# Environment preparation

mkdir -p ${TARGET}

# Generation of the certificates

if [ "${CERT_GENERATE}" == "yes" ]
then
	echo "Generation of certificates..."
	openssl req -x509 -nodes -days 365 \
		-newkey rsa:2048 \
		-subj "${CERT_SUBJECT}" \
		-keyout ${TARGET}/server.key \
		-out ${TARGET}/server.crt

# Copy of certificates
else
	echo "Copy of certificates..."
	cp ${CERT_PEM} ${TARGET}/server.pem
	cp ${CERT_KEY} ${TARGET}/server.key
fi

# Generation of the confiration file

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
