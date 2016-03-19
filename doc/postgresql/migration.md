## Remaining actions

* [x] add Flyway dependency
* [x] add Postgresql dependency
* [x] remove all DB migration actions
* [x] replace DBInit startup hooks by Flyway callbacks - DBStartup deleted
* [x] remove H2
* [ ] use Postgresql for integration tests
* [ ] use Postgresql for development - Docker
* [ ] use Postgresql for development - instance
* [ ] use Postgresql for acceptance - Docker
* [ ] use Postgresql for production

## Spring Boot integration

At startup, the `flywayInitializer` expects to act against an already configured database. Add the following option:

```yaml
flyway:
  baseline-on-migrate: true
```
