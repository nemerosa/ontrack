Nginx Docker container
======================

Goal: defining a Docker container that acts as a SSL proxy to Ontrack.

### Usage

Building the image:

    docker build -t="nginx" .

Running the image:

    docker run -d --cidfile=nginx.cid nginx

Running and connecting to the container:

    docker run -t -i  nginx /bin/bash
