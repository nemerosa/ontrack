# Properties

Each property folder contains the following files:

* `Icon.js` - required - the icon to use for the property
* `Display.js` - required - displaying the property value

Takes the `property` as a parameter.

* `Form.js` - required - the form to edit the property
    * prefix - form prefix
    * property - property
    * entity - holding entity
    * form - form

* `FormPrepare.js` - optional - preparing the values for the GraphQL call
