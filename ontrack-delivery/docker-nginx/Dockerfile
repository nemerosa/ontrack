# Base
FROM ubuntu:14.04

# Meta-information
MAINTAINER Damien Coraboeuf <damien.coraboeuf@gmail.com>

# Install Nginx
RUN \
  apt-get update \
  && apt-get install -y software-properties-common \
  && add-apt-repository -y ppa:nginx/stable \
  && apt-get update \
  && apt-get install -y nginx \
  && echo "\ndaemon off;" >> /etc/nginx/nginx.conf \
  && chown -R www-data:www-data /var/lib/nginx

# Sets the configuration file
ADD proxy.conf /etc/nginx/sites-enabled/

# TODO Sets the certificates

# Exposing port
EXPOSE 443

# Starts Nginx
CMD ["nginx"]
