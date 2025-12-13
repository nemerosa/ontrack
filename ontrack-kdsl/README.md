KDSL Connector
==============

Low level client to the Ontrack API and generated Apollo GraphQL calls.

## Generating the Apollo GraphQL client

Run Ontrack locally and run the [`ontrack.graphql.sh`](ontrack.graphql.sh) script.

Then generate the Apollo classes by running:

```
./gradlew :ontrack-kdsl:generateApolloSources
```
