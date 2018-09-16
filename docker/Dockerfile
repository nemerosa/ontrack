#### Ontrack official image
#
#### Database configuration
#
# The default database connection is set to jdbc:postgresql://postgresql/ontrack, with ontrack as a user and ontrack
# as a password). Those default settings are defined in ontrack-ui/src/main/resources/config/application.yml
#
# In order to override those settings, place an `application.yml` file in the `/var/ontrack/conf` volume, with the
# following content (change the settings of course):
#
# spring:
#     datasource:
#        url: "jdbc:postgresql://postgresql/ontrack"
#        username: ontrack
#        password: ontrack

# Base
FROM openjdk:8u181-jdk

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

# Additional arguments to pass on the command lines
ENV ONTRACK_ARGS ''

# Copies the Ontrack JAR
COPY ontrack.jar /opt/ontrack/

# Starting script
ADD start.sh /opt/ontrack/start.sh

# Running the application
WORKDIR /opt/ontrack
ENTRYPOINT ["/opt/ontrack/start.sh"]

# Healthcheck
HEALTHCHECK --interval=1m --timeout=10s \
  CMD curl -f http://localhost:8080/info || exit 1
