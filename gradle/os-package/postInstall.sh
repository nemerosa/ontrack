#!/usr/bin/env bash

useradd ontrack

mkdir -p /usr/lib/ontrack
chown ontrack:ontrack /usr/lib/ontrack

mkdir -p /var/log/ontrack
chown ontrack:ontrack /var/log/ontrack

# service ontrack restart
