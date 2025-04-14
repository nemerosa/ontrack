#!/bin/bash

curl http://localhost:8800/manage/graphql --output ontrack.graphql

curl http://localhost:8800/manage/graphql-json --output src/main/graphql/net/nemerosa/ontrack/kdsl/connector/graphql/schema/schema.json
