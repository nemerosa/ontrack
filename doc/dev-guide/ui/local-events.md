# UI local page events

In a composite page, like the Home page, where several components are lined up independently of each other, one event in one component may after the content of another component.

For example:

* we have a list of projects, each of them displaying its name and if it is selected as favorite or not. The user can select or unselect a project as a favorite from this component.
* on the same page, we also have a list of the favorite projects, also with a way for the user can select or unselect a project as a favorite from this component.
* whenever a project is selected or unselected as a favorite from one component, we want the other component to be refreshed as well
* since these components do not know of each other, how do we communicate the change?

## Chosen option

Using a local in-memory bus. A component registering an action emits an _event_ with a _name_ and a set of _values_.

Other components can register to _events_ by _name_ and react to the _values_ (for example by refreshing their content).

## Implementation

An [`EventsContexts`](../../ontrack-web-core/components/common/EventsContext.js) context is registered in [all pages](pages/_app.js).

Any component which needs to emit events need to access this context and fire events. For example:

```javascript
const eventsContext = useContext(EventsContext)
eventsContext.fireEvent("event.name", {...values})
```

To react to an event, a component can either use directly the `subscribeToEvent` method:

```javascript
const eventsContext = useContext(EventsContext)
eventsContext.subscribeToEvent("event.name", (values) => {
    // Does something  
})
```

or, when only wanting to refresh a call:

```javascript
const refreshCount = useEventForRefresh("event.name")
// The refreshCount can be used as a dependency for any React.useEffect:
useEffect(() => {}, [refreshCount])
```
