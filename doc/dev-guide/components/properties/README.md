# Properties

Properties are meta-information which can be attached to [_project entities_](../core/README.md).

## Developing a property

Properties are code components with facets both in the backend and the frontend.

### Backend

Any property must be coded in as [extension](../extensions/README.md) of the `PropertyType<T>` type.

Typically, implementations will extend the `AbstractPropertyType<T>` abstract class.

The `<T>` parameter represents the actual type of what needs to be attached to the project entities and the `PropertyType<T>` is responsible for:

* converting to/from this data to frontend API (as JSON)
* converting to/from this data to the storage (as JSON)

The ID (or type) of a property type is used in many places to identify which property is considered.

It's always the FQCN of the implemented `PropertyType` and cannot be configured.

#### Documentation considerations

All properties are exported by default to the user documentation.

The `description` of the property type is used to describe the property and the `T` property class is used to describe the fields that a client must provide to an API.

#### GraphQL considerations

All properties are available by default through the generic properties API (read & write).

For specific GraphQL mutations, the `PropertyMutationProvider` interface must be implemented as a component.

#### CasC considerations

TBD

### Frontend

Each property, to be rendered on the frontend, must provide two components in the `components/framework/properties/<folder>` folder, where `<folder>` is the FQCN of the property type class, after the `net.nemerosa.ontrack.extension`:

* `Icon.js` - exports a default function which returns the icon part of a property. It can be as simple as:

```javascript
import {FaTags} from "react-icons/fa";

export default function Icon() {
    return <FaTags/>
}
```

* `Display.js` - exports a default function which returns the React component used to display the _value_ of the property. It takes as arguments the `property` object, which contains, in its `value` field, the JSON representation of the property type (`T`).

Example:

```javascript
import {Tag} from "antd";

export default function Display({property}) {
    return (
        <>
            <Tag color="green">{property.value.name}</Tag>
        </>
    )
}
```
