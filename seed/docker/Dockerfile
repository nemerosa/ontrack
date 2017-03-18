# Base
FROM java:openjdk-8u111-jdk

# Makes sure all tools for the build are installed
RUN apt-get update && apt-get install -y \
    git \
    subversion \
    zip \
    xvfb \
    iceweasel \
    graphviz \
    && rm -rf /var/lib/apt/lists/*

# Installing Docker
RUN wget https://get.docker.com/builds/Linux/x86_64/docker-1.13.0.tgz -O docker.tgz
RUN tar -xvzf docker.tgz
RUN mv docker/* /usr/bin/
RUN chmod +x /usr/bin/docker

# Installs Docker Compose
RUN curl --fail --silent -L https://github.com/docker/compose/releases/download/1.10.1/docker-compose-`uname -s`-`uname -m` > /usr/bin/docker-compose
RUN chmod +x /usr/bin/docker-compose

# Installs Docker Machine
RUN curl --fail --silent -L https://github.com/docker/machine/releases/download/v0.9.0/docker-machine-`uname -s`-`uname -m` > /usr/bin/docker-machine
RUN chmod +x /usr/bin/docker-machine

# Git configuration
RUN git config --global user.email "jenkins@nemerosa.net" \
    && git config --global user.name "Jenkins"
