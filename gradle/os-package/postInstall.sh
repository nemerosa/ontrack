#!/usr/bin/env bash

useradd ontrack

mkdir -p /usr/lib/ontrack
mkdir -p /var/log/ontrack

chown ontrack:ontrack /opt/ontrack
chown ontrack:ontrack /usr/lib/ontrack
chown ontrack:ontrack /var/log/ontrack
