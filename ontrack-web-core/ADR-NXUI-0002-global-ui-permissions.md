`ADR-NXUI-0002-global-ui-permissions` - Global UI permissions
=============================================================

# Context

In order to enable some features at client side (UI), we need to know which permissions are granted to the current user.

# Chosen option

The [`UserContextProvider`](components/providers/UserProvider.js) is injected at [top-level](pages/_app.js) and stores the results of the `rest/user` REST call into a `user.authorizations` context.

The `user.authorizations` is a map:

* key: domain name, for example: `project`
* value: map of action x boolean, for example: `create` ==> `true`

# Usage

Inject the context:

```javascript
const user = useContext(UserContext)
```

Then check the authorizations as needed:

```javascript
if (user.authorizations.project?.create) {
    // ...
}
```

# Implementation notes

The authorizations are filled in at server side using the
`AuthorizationContributor` server and in particular its extensible
variant `ExtensionAuthorizationContributor` through the
`AuthorizationContributorExtension` extension points.
