# Base
FROM openjdk:8u121-jdk

# Directory for the test results
VOLUME /ontrack/acceptance/results

RUN mkdir -p /ontrack/acceptance/app

# Startup script
COPY acceptance.sh /ontrack/acceptance/app/
RUN chmod a+x /ontrack/acceptance/app/acceptance.sh

# Acceptance JAR
COPY ontrack-acceptance.jar /ontrack/acceptance/app/

# General Java options
ENV JAVA_OPTIONS ''

# Selenium URL
ENV ONTRACK_ACCEPTANCE_SELENIUM_URL ''

# Target Ontrack URL
ENV ONTRACK_ACCEPTANCE_TARGET_URL 'http://localhost:8080'

# Target Ontrack admin password
ENV ONTRACK_ACCEPTANCE_TARGET_ADMIN_PASSWORD 'admin'

# Target Ontrack SSL disabled?
ENV ONTRACK_ACCEPTANCE_TARGET_SSL_DISABLED 'false'

# Acceptance test context
ENV ONTRACK_ACCEPTANCE_CONTEXT 'all'

# Acceptance timeout
ENV ONTRACK_ACCEPTANCE_TIMEOUT '120'

# Acceptance implicit wait
ENV ONTRACK_ACCEPTANCE_IMPLICIT_WAIT '5'

# Running the acceptance application
ENTRYPOINT /ontrack/acceptance/app/acceptance.sh
