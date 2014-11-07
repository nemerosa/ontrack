# Base
FROM ubuntu:14.04

# Meta-information
MAINTAINER Damien Coraboeuf <damien.coraboeuf@gmail.com>

# Mount points
VOLUME /etc/nginx/sites-enabled
VOLUME /etc/nginx/ssl
VOLUME /var/log/nginx

# Install Nginx
RUN \
  apt-get update \
  && apt-get install -y software-properties-common \
  && add-apt-repository -y ppa:nginx/stable \
  && apt-get update \
  && apt-get install -y nginx \
  && echo "\ndaemon off;" >> /etc/nginx/nginx.conf \
  && chown -R www-data:www-data /var/lib/nginx

# Exposing port
EXPOSE 443

# Starts Nginx
CMD ["nginx"]
