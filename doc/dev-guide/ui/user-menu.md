# User menu

The user menu contains global items available for the current user.

Adding an entry is done at server side by declaring an `UserMenuItemExtension` component.

This extension declares a list of `UserMenuItem` objects, each
having the following properties:

* `groupId` - ID of the group the item belongs to (see below)
* `extension` - ID of the extension feature
* `id` - ID of the menu item
* `name` - display name for the menu item

For example:

```kotlin
UserMenuItem(
    groupId = CoreUserMenuGroups.CONFIGURATIONS,
    extension = notificationsExtensionFeature,
    id = "subscriptions/global",
    name = "Global subscriptions",
)
```

## Group

The `groupId` refers to a group of user menu items.

Core groups can be referred to using the `CoreUserMenuGroups` object, like
in the example above.

> Extensions can also contribute new groups, by providing an `UserMenuGroupExtension` component.

## Conversion to path

The user menu item refers to a page whose path is computed using:

```
/extension/${extension}/${id}
```

> In the example above, the path would therefore be
> `/extension/notifications/subscriptions/global` because
> the ID of the `notificationsExtensionFeature` is `notifications`.

## Display icon

By default, groups and menu items are associated with no icon.

To set an icon for a menu item, edit the `itemIcons` object in the
`UserMenu.js` file.

To set an icon for a group, edit the `groupIcons` object in the
`UserMenu.js` file.
