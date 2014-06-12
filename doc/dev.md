ontrack developer documentation
===============================

## Technology stack

### Client side:

One page only, pure AJAX communication between the client and the server.

* AngularJS
* Angular UI Router
* Angular UI Bootstrap
* Bootstrap
* Less

### Server side:

* Spring Boot for the packaging & deployment
* Spring MVC for the REST API
* Spring for the IoC
* Spring Security & AOP for the security layer
* Plain JDBC for the data storage
* H2 in MySQL mode for the database engine

## Layers

* HTML / CSS
* JS

* UI
* Service / Model
* Repository

* Database

## UI

### Resources

The UI is realized by REST controllers. They manipulate the _model_ and gets access to it through _services_.

In the end, the controllers return _model_ objects that must be decorated by links in order to achieve Hateoas.

The controllers are not directly responsible for the decoration of the model objects as _resources_ (model + links).

## Running the application in development mode

1. In `ontrack-web`, run `grunt watch`
1. In `ontrack-ui`, run the `Application` main class with `--spring.profiles.active=dev` as argument.

When you need to restart the application, just restart the Java part - you can let the `grunt watch` run as long
 as you do not add any new Javascript file. When you add a new JS file (or delete one), the `watch` task should be
 restarted.
 
Please note that Intellij IDEA allows a good integration with Grunt.

## Glossary

**Form**

Creation or update _links_ can be accessed using the `GET` verb in order to get a form that allows the client to
carry out the creation or update.

Such a form will give information about:

* the fields to be created/updated
* their format
* their validation rules
* their description
* their default or current values
* etc.

The GUI can use those forms in order to automatically (and optionally) display dialogs to the user. Since the model
is responsible for the creation of those forms, this makes the GUI layer more resilient to the changes.

**Link**

In _resources_, links are attached to _model_ objects, in order to implement a HATEOAS principle in the application
interface.

HATEOS does not rely exclusively on HTTP verbs since this would not allow a strong implementation of the actual
 use cases and possible navigations (which HATEOAS is all about).
 
For example, the "Project creation" link on the list of projects is _not_ carried by the sole `POST` verb, but by
a `_create` link. This link can be accessed through verbs:

* `OPTIONS` - list of allowed verbs
* `GET` - access to a form that allows to create the object
* `POST` or `PUT` for an update - actual creation (or update) of the object 

**Model**

Representation of a concept in the application. This reflects the _ubiquitous language_ used throughout the application,
and is used in all layers. As POJO on server side, and JSON objects at client side.

**Repository**

Model objects are persisted, retrieved and deleted through repository objects. Repositories act as a transparent 
persistence layer and hides the actual technology being used.

**Resource**

A resource is a model object decorated with links that allow the client side to interact with the API following
the HATEOAS principle. More than just providing access to the model structure, those links reflect the actual
use cases and the corresponding navigation. In particular, the links are driven by the authorizations (a "create"
link not being there if the user is not authorized). See _Link_ for more information.

**Service**

Services are used to provide interactions with the model.
