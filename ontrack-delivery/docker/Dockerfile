# Base
FROM ubuntu:14.04
MAINTAINER Damien Coraboeuf <damien.coraboeuf@gmail.com>

# JDK8
RUN apt-get update
RUN apt-get install -y software-properties-common
RUN add-apt-repository -y ppa:webupd8team/java
RUN apt-get update
RUN echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections
RUN apt-get install -y oracle-java8-set-default

# Directory set-up
RUN mkdir -p /opt/ontrack

# Copies the Ontrack JAR
ADD ontrack/ontrack.jar /opt/ontrack/

# Exposing the port
EXPOSE 8080

# TODO Running the application
# CMD ["java", "-jar", "/opt/ontrack/ontrack.jar", "--spring.profiles.active=prod", "--logging.path=/opt/ontrack/log/"]
