Docker implementation
=====================

## Overview

The `Dockerfile` makes sure to:

* have a base Ubuntu 64 image
* install JDK8
* create the `/opt/ontrack` environment
* copy the Ontrack executable JAR from a local source in the non versioned local `ontrack` directory. The JAR 
must be put in this directory before the Docker image is created (this can be automated).

## nginx

This implementation does not install an `nginx` proxy. The idea is to have `nginx` run as a separate container 
that links to the `ontrack` container, in a pure Docker fashion.
