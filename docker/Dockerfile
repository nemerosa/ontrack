# Base
FROM java:openjdk-8u111-jdk

# Meta-information
MAINTAINER Damien Coraboeuf <damien.coraboeuf@gmail.com>

# Git installation
RUN apt-get install -y git

# Directory set-up
RUN mkdir -p /opt/ontrack

# Data volume
VOLUME /var/ontrack/data

# Configuration volume
VOLUME /var/ontrack/conf

# Directory which contains the extensions
VOLUME /var/ontrack/extensions

# Variable to host the directory files
# WARNING: This MIGHT be different than the volume, depending on how this image
# is used, with an external volume or in a child Dockerfile
ENV EXTENSIONS_DIR /var/ontrack/extensions

# Exposing the ports
EXPOSE 443
EXPOSE 8080

# Profile
ENV PROFILE prod

# Java options
ENV JAVA_OPTIONS ''

# Copies the Ontrack JAR
COPY ontrack.jar /opt/ontrack/

# Starting script
ADD start.sh /opt/ontrack/start.sh

# Running the application
WORKDIR /opt/ontrack
CMD ["sh", "/opt/ontrack/start.sh"]
