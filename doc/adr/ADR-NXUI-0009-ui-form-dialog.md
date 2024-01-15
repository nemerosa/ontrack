`ADR-NXUI-0009-ui-form-dialog` - Using modal form dialogs
=========================================================

# Context

Modal form dialogs are used to ask the user for information and input in a blocking way.

# Usage

## Define the dialog

Preferably in a separate file than the client one, define:

* a state:

```javascript
export const useMyDialog = ({/* Configuration */}) => {
    return useFormDialog({
        // Specific configuration
    })
}
```

* a dialog component:

```javascript
export default function MyDialog({myDialog}) {
    return (
        <>
            <FormDialog dialog={myDialog}>
                {/* Form items */}     
            </FormDialog>
        </>
    )
}
```

## Declare the dialog

In the client component:

* define the dialog state:

```javascript
const myDialog = useMyDialog({/* Specific configuration */})
```

* declare the dialog component:

```javascript
return (
    <>
        {/* ... */}
        <MyDialog myDialog={myDialog}/>
    </>
)
```

* to open the dialog (on an action for example):

```javascript
myDialog.start({/* Context */})
```

# Configuration

The `useFormDialog` has the following configuration parameters.

| Parameter | Type                    | Default | Description                                                       |
|-----------|-------------------------|---------|-------------------------------------------------------------------|
| init      | (form, context) => {}   | _None_  | Used to initialize the dialog form before it's shown to the user. |
| onSuccess | (values, context) => {} | _None_  | Validations of the values.                                        |


# Context

The _context_ is an object which is passed to the dialog by the client, typically used to initialize the content of the form.

It is passed to form fields through the configuration `init` method. For example:

```javascript
useFormDialog({
    init: (form, context) => {
        form.setFieldValue('name', context.name)
    }
})
```

On the other side of the validation, when the form is validated and OK, the context is passed to the `onSuccess` method:

```javascript
useFormDialog({
    onSuccess: (values, context) => {}
})
```

Both the _current_ values of the form and the _initial context_ are passed.

# Passing information to the dialog

The `init` method may load some information and need to pass it to the dialog component. Use a local state
and pass the state to the configuration.

In the `useFormDialog`:

```javascript
const [promotionLevel, setPromotionLevel] = useState()
return useFormDialog({
    init: (form, context) => {
        // Loads information
        // ...
        // Sets the state
        setPromotionLevel(/* ... */)
    },
    promotionLevel, // <-- registers the state
})
```

Use the state into the component:

```javascript
export default function MyDialog({myDialog}) {
    return (
        <>
            <FormDialog dialog={myDialog}>
                {
                    myDialog.promotionLevel && /* ... */
                }
            </FormDialog>
        </>
    )
}

```

# GraphQL integration

In some cases, the whole dialog is just about orchestrating some GraphQL calls.

The mutation to call on the dialog success is passed into the `query` parameter while the `userNode` is used to identify the field which contains the user errors.

For example, for the creation of a project:

```javascript
return useFormDialog({
    // ...
    prepareValues: (values) => {
        values.description = values.description ? values.description : ''
        values.disabled = values.disabled ? values.disabled : false
    },
    query: gql`
            mutation CreateProject(
                $name: String!,
                $description: String,
                $disabled: Boolean!,
            ) {
                createProject(input: {
                    name: $name,
                    description: $description,
                    disabled: $disabled,
                }) {
                    errors {
                        message
                    }
                }
            }
        `,
    userNode: 'createProject',
})
```

> Note the use of `prepareValues` to prepare the list of variables for the GraphQL query.

In some cases, the query and the node can be dynamic, linked to the context. For these cases, the `query` and the `userNode` can be functions of the context:

```javascript
return useFormDialog({
    query: (context) => { /* ... */ },  
    userNode: (context) => { /* ... */ },  
})
```