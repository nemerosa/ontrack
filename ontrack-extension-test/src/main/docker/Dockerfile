# Base Ontrack image
FROM nemerosa/ontrack:@ontrackVersion@

# Specific extension folder
ENV EXTENSIONS_DIR /var/ontrack/test

# Copies the extensions
ADD *.jar /var/ontrack/test/

# Entry point does not change

# Healthcheck inherited
