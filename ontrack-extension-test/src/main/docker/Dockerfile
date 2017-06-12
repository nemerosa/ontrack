# Base Ontrack image
# TODO Version as a parameter
FROM nemerosa/ontrack:2.30.8

# Specific extension folder
ENV EXTENSIONS_DIR /var/ontrack/test

# Copies the extensions
ADD *.jar /var/ontrack/test/

# Configuration
ADD application.yml /var/ontrack/conf/

# Entry point does not change

# Healthcheck
HEALTHCHECK --interval=1m --timeout=10s \
  CMD curl -f http://localhost:8080/info || exit 1
