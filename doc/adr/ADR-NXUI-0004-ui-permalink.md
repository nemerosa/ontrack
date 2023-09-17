`ADR-NXUI-0004-ui-permalink` - UI permalink
===========================================

# Context

The browser URL can be used to store temporary data in order to create _permalinks_ which can be shared between users to quickly activate some preferences or settings.

# Chosen option

The [Next JS router](https://nextjs.org/docs/pages/api-reference/functions/use-router) can be used to access its `query` variables:

```javascript
const router = useRouter()
const {my_field} = router.query
// my_field can be undefined
```

To set a query field in the URL, the component can use:

```javascript
router.replace({
    pathname: `your/path`,
    query: {my_field: my_value}
}, undefined, {shallow: true})
```
