# Base
FROM ubuntu:xenial

# Meta-information
MAINTAINER Damien Coraboeuf <damien.coraboeuf@gmail.com>

# JDK installation
RUN apt-get update && apt-get install -y openjdk-8-jdk

# Exposing the ports
EXPOSE 8080

# Gets the Debian file in this image
COPY ontrack.deb /opt/ontrack/

# Installs it
RUN dpkg -i /opt/ontrack/ontrack.deb

# Gets the application.yml for local configuration
COPY application.yml /usr/lib/ontrack/

# Starting point provided by CI/CD infra
