# Base
FROM ubuntu:14.04

# Meta-information
MAINTAINER Damien Coraboeuf <damien.coraboeuf@gmail.com>

# JDK8 APT set-up
RUN apt-get update \
    && apt-get install -y software-properties-common \
    && add-apt-repository -y ppa:webupd8team/java \
    && apt-get update

# JDK8 installation
RUN echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections
RUN apt-get install -y oracle-java8-set-default

# Directory set-up
RUN mkdir -p /opt/ontrack

# External volume set-up and links
RUN mkdir -p /opt/ontrack/mount/database
RUN mkdir -p /opt/ontrack/mount/work
RUN mkdir -p /opt/ontrack/mount/log
VOLUME /opt/ontrack/mount
RUN ln -s /opt/ontrack/mount/database /opt/ontrack/database
RUN ln -s /opt/ontrack/mount/work /opt/ontrack/work
RUN ln -s /opt/ontrack/mount/log /opt/ontrack/log

# Exposing the port
EXPOSE 8080

# Copies the Ontrack JAR
COPY ontrack/ontrack.jar /opt/ontrack/

# Running the application
# WORKDIR /opt/ontrack
# CMD ["java", "-jar", "ontrack.jar", "--spring.profiles.active=prod", "--logging.path=log/"]
