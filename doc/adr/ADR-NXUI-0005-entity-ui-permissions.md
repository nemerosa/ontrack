`ADR-NXUI-0005-entity-ui-permissions` - Entity UI permissions
=============================================================

# Context

In order to enable some features at client side (UI) for a given entity (project, branch, etc.), we need to know which permissions are granted to the current user for this very entity..

# Chosen option

Entities expose an `authorizations` field in their GraphQL type, containing the following fields:

* `name` - scope of the authorization (`build` for example)
* `action` - action to perform on the scope (`promote` for example)
* `authorized` - `Boolean` indicating the authorization

# Example

To know if a _Build_ can be promoted, we first get the `authorizations` on the build:

```graphql
build(...) {
    authorizations {
        name
        action
        authorized
    }
}
```

Then, you can use the  function:

```javascript
import {isAuthorized} from "@components/common/authorizations";

if (isAuthorized(build, 'build', 'promote')) {
    // ...
}
```

# Implementation notes

On the server side, authorizations at entity level are provided by components implementing the `AuthorizationContributor` interface.

This is plugged at GraphQL type level by injecting the `gqlInterfaceAuthorizableService: GQLInterfaceAuthorizableService` service in the `GQLType` and registering the `authorizations` field:

```kotlin
// ...    
.apply {
    gqlInterfaceAuthorizableService.apply(this, Build::class)
}
// ...
```

# See also

* [ADR-NXUI-0002-global-ui-permissions](ADR-NXUI-0002-global-ui-permissions.md)
