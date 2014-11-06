Nginx Docker container
======================

Goal: defining a Docker container that acts as a SSL proxy to Ontrack.

### Basic usage

Prepare the files needed by the Docker image:

    ./nginx.sh

Build the image:

    docker build -t="nginx" .

Running the image:

    docker run -d --cidfile=nginx.cid nginx

This will generate an nginx instance that connects to the 127.0.0.1 on port 8080, using SSL generated and
auto signed certificates.

To know the other options, type `./nginx.sh --help`.

Running and connecting to the container:

    docker run -t -i  nginx /bin/bash
