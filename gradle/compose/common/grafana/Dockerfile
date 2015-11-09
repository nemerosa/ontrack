# Grafana base image
FROM grafana/grafana:latest

# Default configuration
COPY grafana.ini /etc/grafana/

# Copies the initial database
COPY grafana.db /var/lib/grafana/

# Default Ontrack dashboard
COPY Ontrack.json /var/lib/grafana/dashboards/
