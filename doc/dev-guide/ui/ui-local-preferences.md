# UI local preferences

While some preferences can be stored in the user profile (see [UI user preferences`](ui-local-preferences.md)), other
ones can be stored locally, in the
browser [local storage](https://developer.mozilla.org/en-US/docs/Web/API/Window/localStorage).

Components should not access the `localStorage` API directly but use instead of
the [`local.js`](../../ontrack-web-core/components/storage/local.js) utility methods.

For example, to check if the user wants to group validation stamps per status in the branch page:

```javascript
import {isGroupingValidationStamps} from "@components/storage/local";

if (isGroupingValidationStamps()) {
    // ...
}

// and to set the preference
setGroupingValidationStamps(true)
```

Each preference token must be associated with its own `set` / `get/is` methods.

## See also

* [UI user preferences](ui-user-preferences.md) - for persistent preferences
