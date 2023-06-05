Web static resources
====================

In development mode, one must run the `net.nemerosa.ontrack.boot.Application` in `dev` profile mode, by
adding the following program arguments:

    --spring.profiles.active=dev

Additionally, one must compile the Web static resources in `dev` mode by running:

    ./gradlew dev

You can also monitor the changes in web resources and make the browser reload the page automatically by running:

    ./gradlew watch

This command is blocking.

> Note that your browser must support [LiveReload](http://livereload.com/).
