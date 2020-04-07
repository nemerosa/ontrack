# Base
FROM azul/zulu-openjdk:11.0.6

# Makes sure all tools for the build are installed
RUN apt-get update && apt-get install -y \
    git \
    zip \
    rsync \
    curl \
    graphviz \
    python-setuptools \
    && rm -rf /var/lib/apt/lists/*

# S3 Cmd line tool
RUN curl --fail --silent -L https://github.com/s3tools/s3cmd/releases/download/v2.0.2/s3cmd-2.0.2.tar.gz --output s3cmd.tar.gz \
    && tar -xvzf s3cmd.tar.gz \
    && cd s3cmd-2.0.2 \
    && python setup.py install

# Installing Docker
RUN curl --fail --silent -L https://download.docker.com/linux/static/stable/x86_64/docker-18.09.3.tgz --output docker.tgz
RUN tar -xvzf docker.tgz
RUN mv docker/* /usr/bin/
RUN chmod +x /usr/bin/docker

# Installs Docker Compose
RUN curl --fail --silent -L https://github.com/docker/compose/releases/download/1.25.4/docker-compose-`uname -s`-`uname -m` > /usr/bin/docker-compose
RUN chmod +x /usr/bin/docker-compose

# Installs Docker Machine
RUN curl --fail --silent -L https://github.com/docker/machine/releases/download/v0.16.2/docker-machine-`uname -s`-`uname -m` > /usr/bin/docker-machine
RUN chmod +x /usr/bin/docker-machine

# Git configuration
RUN git config --global user.email "jenkins@nemerosa.net" \
    && git config --global user.name "Jenkins"
