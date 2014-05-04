Web static resources
====================

In development mode, one must run the `net.nemerosa.ontrack.boot.Application` in `dev` profile mode, by
adding the following program arguments:

    --spring.profiles.active=dev

Additionally, one must compile the Web static resources in `dev` mode by running a Grunt command into the
`ontrack-web` module:

    grunt dev

or:

    grunt watch

By running `watch`, this makes the static resources changes available to the browser using LiveReload.
