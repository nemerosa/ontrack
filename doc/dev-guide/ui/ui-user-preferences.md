# UI preferences

One user may select some options in the UI which are then saved in their preferences, to be reused in a later session.

Preferences are accessed using the `preferences` GraphQL query:

```graphql
query GetPreferences {
    preferences {
        # Fields that your component needs
    }
}
```

Preferences can be changed using the `setPreferences` GraphQL mutation:

```graphql
mutation SetPreferences {
    setPreferences(input: {
        # Fields
    }) {
        errors {
            message
        }
    }
}
```

## Convenience method

Instead of calling the GraphQL API directly, one component can use the `usePreferences` method:

```javascript
const preferences = usePreferences()
```

The returned value is an object with all preferences fields.

To set back some preferences, the component can call:

```javascript
preferences.setPreferences({
    field1: newValue1,
    // Other fields if needed
})
```

## Exceptions

* [ ] Selected dashboard on the home page is set using the `selectDashboard` mutation - this should be replaced by the `setPreferences` mutation

## See also

* [UI local preferences](ui-local-preferences.md)
