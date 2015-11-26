Plan
====

1. cutting the link between the core and the extensions (some integration tests are using those extensions) - extensions are still needed at runtime from the UI
1. having resources (but Less) served statically from extension modules (by using the `/static` classpath folder)
1. having `app.js` and `index.html` take into account the extensions dynamically
1. building the core + plugin only
1. building the extensions (.opi ?)
1. building the package (core boot JAR + extensions ZIP)
1. loading the extensions at startup (sonar-classloader + child application contexts)


