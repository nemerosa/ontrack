# Page tools

In a page, one can declare a series of "tools" which will be displayed in a _Tools_ dropdown in the command bar.

## Declaration of the Tools command

In the page, the _Tools_ dropdown must be registered among the other commands:

```javascript
const commands = [
    <UserMenuActions key="userMenuActions" actions={project.userMenuActions}/>,
    // ...
]
```

and then declared in the page:

```jsx
<MainPage
    {/* ... */}
    commands={commands}
>
```

## GraphQL `userMenuActions`

The `<UserMenuActions>` component uses a list of user menu actions which are typically made available
through GraphQL. For example, for a project:

```javascript
import {gqlUserMenuActionFragment} from "@components/services/fragments";
gql`{
    project {
        userMenuActions {
            ...userMenuActionFragment
        }
    }

    ${gqlUserMenuActionFragment}
}`
```

On the server side, the `userMenuActions` field must be declared:

```kotlin
private val extensionManager: ExtensionManager
// ...
private val projectEntityUserMenuItemExtensions: Set<ProjectEntityUserMenuItemExtension> by lazy {
    extensionManager.getExtensions(ProjectEntityUserMenuItemExtension::class.java).toSet()
}
// ...
GraphQLFieldDefinition.newFieldDefinition()
    .name("userMenuActions")
    .description("List of actions available for this entity")
    .type(listType(GQLTypeUserMenuAction.TYPE))
    .dataFetcher { env ->
        val entity: T = env.getSource()
        projectEntityUserMenuItemExtensions.flatMap { extension ->
            extension.getItems(entity)
        }
    }
    .build()
```

> Note that this field is automatically available for all _project entities_ (projects, branches, builds, ...)

## Project entity user menu item extensions

Still on the server side, the only thing remaining to declare a menu entry for a given entity type is
to declare a `ProjectEntityUserMenuItemExtension` component.

> See `ProjectAutoVersioningUserMenuItemExtension` for an example.
