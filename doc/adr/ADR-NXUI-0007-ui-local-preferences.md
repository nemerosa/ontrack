`ADR-NXUI-0007-ui-local-preferences` - UI local preferences
===========================================================

# Context

While some preferences can be stored in the user profile (see [`ADR-NXUI-0003-ui-preferences`](ADR-NXUI-0003-ui-preferences.md)), other ones can be stored locally, in the browser [local storage](https://developer.mozilla.org/en-US/docs/Web/API/Window/localStorage).

# Chosen option

Components should not access the `localStorage`
 API directly but use instead of the [`local.js`](../../ontrack-web-core/components/storage/local.js) utility methods.

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

# See also

* [`ADR-NXUI-0003-ui-preferences`](ADR-NXUI-0003-ui-preferences.md) - for persistent preferences
