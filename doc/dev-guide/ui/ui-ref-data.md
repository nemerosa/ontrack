# UI reference data

## Context

Some reference data (like the list of _validation run statuses_ and their transitions) need to be made accessible to the UI components at all times.

By definition, this reference data should be immutable (at least for the mean duration of a UI session).

## Chosen option

A `RefDataContext` is made available at the top-level of the application and accessible from any component using a `useRefData()` wrapper method.

For example, to get the list of _validation run statuses_ and their transitions:

```javascript
const refData = useRefData()
const validationRunStatuses = refData.validationRunStatuses
```

## References

* [`RefDataProvider.js`](../../../ontrack-web-core/components/providers/RefDataProvider.js)
