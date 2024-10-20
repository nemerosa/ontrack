# Calling the GraphQL API

Components need to call the Ontrack GraphQL API.

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

## Mutations

Mutations are usually called upon an action initiated by a user, typically in an `async` function.

Errors can be processed automatically by doing:

```javascript
const onAction = async () => {
    const data = await client.request(
        gql`
            mutation {
                doSomething {
                    errors {
                        message
                    }
                }
            }
        `
    )
    if (processGraphQLErrors(data, 'doSomething')) {
        // Success
    }
}
```

If errors must be explicitly collected (for a form for example):

```javascript
const data = await client.request(/*...*/)
const errors = getGraphQLErrors(data, 'doSomething')
if (errors) {
    // There are some errors
}
```
