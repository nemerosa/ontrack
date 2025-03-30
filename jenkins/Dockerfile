# Base
FROM azul/zulu-openjdk:17

# Makes sure all tools for the build are installed
RUN apt-get update && apt-get install -y \
    software-properties-common \
    zip \
    rsync \
    curl \
    graphviz \
    python3-setuptools

# Git installation
RUN add-apt-repository --yes ppa:git-core/ppa \
    && apt-get update \
    && apt-get install -y git \
    && rm -rf /var/lib/apt/lists/*

# S3 Cmd line tool
RUN curl --fail --silent -L https://github.com/s3tools/s3cmd/releases/download/v2.3.0/s3cmd-2.3.0.tar.gz --output s3cmd.tar.gz \
    && tar -xvzf s3cmd.tar.gz \
    && cd s3cmd-2.3.0 \
    && python3 setup.py install

# Installing Docker
RUN curl --fail --silent -L https://download.docker.com/linux/static/stable/x86_64/docker-28.0.4.tgz --output docker.tgz
RUN tar -xvzf docker.tgz
RUN mv docker/* /usr/bin/
RUN chmod +x /usr/bin/docker

# Installs Docker Compose
RUN curl --fail --silent -L https://github.com/docker/compose/releases/download/v2.34.0/docker-compose-`uname -s`-`uname -m` > /usr/bin/docker-compose
RUN chmod +x /usr/bin/docker-compose

# Git configuration
RUN git config --global user.email "jenkins@nemerosa.net" \
    && git config --global user.name "Jenkins" \
    && git config --global init.defaultbranch main
