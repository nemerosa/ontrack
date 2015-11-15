Development environment for Ontrack
===================================

In order to create a complete environment for Ontrack, this [Docker Compose](http://docs.docker.com/compose/) file
allows you to create:

* an InfluxDB available on ports `38086` (database end point) and `38303` (GUI)
* a Grafana dashboard on port `33000`

You create it by running:

```bash
cd doc/dev && docker-compose up -d
```

In order to configure Ontrack in development to access this environment, create an `application-dev.yml` file in
`ontrack-ui/src/main/resources` with the following content:

```yaml
# Enabling metrics
ontrack:
  metrics:
    influxdb:
      host: 192.168.99.100
      port: 38086

```

where `192.168.99.100` is replaced by your Docker host.
