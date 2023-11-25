`ADR-NXUI-0008-ui-graphql-call` - Calling the GraphQL API
=========================================================

# Context

Components need to call the Ontrack GraphQL API.

# Chosen option

Always use the `useGraphQLClient` hook:

```javascript
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";

const client = useGraphQLClient()
client.request(query, variables).then(data => {
    // ...
}).finally(() => {
    // ...
})
```

If used inside a React `useEffect` hook, the `client` must be set as a dependency and tested for validity:

```javascript
const client = useGraphQLClient()

useEffect(() => {
    if (client) {
        // Using the client
    }
}, [client, /* ... */])
```
