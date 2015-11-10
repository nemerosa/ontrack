# Grafana base image
FROM grafana/grafana:latest

# Dashboards
VOLUME /usr/lib/grafana/dashboards

# Default configuration
COPY grafana.ini /etc/grafana/

# Copies the initial database
COPY grafana.db /var/lib/grafana/
