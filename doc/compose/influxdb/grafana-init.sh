#!/usr/bin/env bash

curl \
    --user admin:admin \
    -X POST \
    -v \
    -H "Content-Type: application/json" \
    --data-binary '{"name":"ontrack","type":"influxdb","access":"proxy","url":"http://influxdb:8086","password":"root","user":"root","database":"ontrack","basicAuth":true,"basicAuthUser":"root","basicAuthPassword":"root","isDefault":false,"jsonData":null}' \
    http://192.168.99.101:32791/api/datasources
