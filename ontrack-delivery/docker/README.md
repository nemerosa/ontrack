Docker implementation
=====================

Docker has a big advantage when used in CI and compared to Vagrant + Puppet: it is tremendously fast!

## Summary

To run create and run the `ontrack` image, having 8080 exposed on the host:

    ./docker-setup.sh --jar=<path to Ontrack JAR> --run --port=8080

`ontrack` data will be stored in the local `mount` directory. In particular, the log file is available in:

    less -f mount/log/spring.log

In order to upgrade `ontrack` to a new version:
    
    # Stops the running container
    docker stop `cat ontrack.cid`
    # Respawn a container with the new JAR file
    ./docker-setup.sh --jar=<path to new Ontrack JAR> --run --port=8080

That's it! The `ontrack` data in the `mount` directory is not touched.    

## Installation of ontrack

To build the `ontrack` image:

    ./docker-setup.sh --jar=<path to Ontrack JAR>

This will create na `ontrack` Docker image whose version is computed from the JAR name (it can also
be forced using `--docker-version=<version>`. The image name can be changed using for example
`--docker-image=nemerosa/ontrack`.

This image can be listed using `docker images` and deleted using `docker rmi <image id>`.

To run the created image and connect to it: `docker run -t -i ontrack:<version> /bin/bash`. Warning: this will create
a container that you can list using `docker ps -a` and delete using `docker rm <container id>`.
 
To run the image in _real mode_:

* `docker run -d -P ontrack:<version>` will publish the container's 8080 port on the host 8080 port
* `docker run -d -p <port>:8080 ontrack:<version>` will publish the container's 8080 port on the host `<port>` port

The created docker container can be listed using `docker ps -a` and deleted using `docker rm <container id>`.

## Implementation details

In order to have a fully running image, one needs the `/opt/ontrack/mount` volume to be mounted:

    docker run \
        -d \
        -P \
        -v `pwd`/mount:/opt/ontrack/mount \
        --cidfile=ontrack.cid \
        ontrack:<version>

This creates a local `ontrack.cid` file that can be used to control the container in an automated way, for example,
to remove it:

    docker rm -f `cat ontrack.cid`
    
Note that the setup sccript can be used to create the image and run it immediately:

    ./docker-setup.sh --jar=<path to Ontrack JAR> --run

Persistent data for Ontrack will be stored in a local `mount` directory (this can be overridden using
`--mount=<dir>`). The created container ID is stored in a local `ontrack.cid` like above. Adding `--bash` to the
command above would _not_ start `ontrack` but will open a Bash session on the container.

When the container is started, you can access `ontrack` by calling:

http://<ip>:<port>

where `<ip>` is the IP of the Docker host, and `<port>` is the forwarded port of the `ontrack` container.

Note that when using [boot2docker](http://boot2docker.io/), you can access the IP by running `boot2docker ip`.

By running the `docker-setup.sh` command above with `--port=<port>`, you will forward the container's 8080 port to
 the indicated `<port>`. So when running with `--port=8080`, you will be able to access Ontrack using http://<ip>:8080.

## nginx link

*To be continued.*

> This implementation does not install an `nginx` proxy. The idea is to have `nginx` run as a separate container that links to the `ontrack` container, in a pure Docker fashion.
