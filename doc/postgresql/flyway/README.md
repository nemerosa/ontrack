Running the migration using the command line, using the filesystem as source:

```
flyway -driver=org.postgresql.Driver \
   -url=jdbc:postgresql://`machine ip build`/ontrack \
   -user=ontrack \
   -password=ontrack \
   -locations=filesystem:sql \
   migrate \
   -X
```

Deleting the current schema:

```
flyway -driver=org.postgresql.Driver \
   -url=jdbc:postgresql://`machine ip build`/ontrack \
   -user=ontrack \
   -password=ontrack \
   -locations=filesystem:sql \
   clean \
   -X
```
