# Base
FROM centos:7.1.1503

# Meta-information
MAINTAINER Damien Coraboeuf <damien.coraboeuf@gmail.com>

# JDK installation
RUN yum install -y java-1.8.0-openjdk-devel.x86_64

# Exposing the ports
EXPOSE 8080

# Log volume
VOLUME /var/log/ontrack

# Gets the RPM file in this image
COPY ontrack.rpm /opt/ontrack/

# Installs it
RUN rpm -i /opt/ontrack/ontrack.rpm

# Gets the application.yml for local configuration
COPY application.yml /usr/lib/ontrack/

# Starting point
ENTRYPOINT ["/opt/ontrack/bin/ontrack.sh"]
CMD ["start"]

# Healthcheck
HEALTHCHECK --interval=1m --timeout=10s \
  CMD curl -f http://localhost:8080/info || exit 1
