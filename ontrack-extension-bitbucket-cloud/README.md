Extension: Bitbucket Cloud
==========================

This extension allows the support for Bitbucket Cloud, namely at https://bitbucket.org

Since the REST API is not the same than Bitbucket Server (2.0 versus 1.0), support for Bitbucket Cloud is done is a separate module than for Bitbucket Server (which stays in the `ontrack-extension-stash` module).

Connections to Bitbucket Cloud are done at workspace level. Ontrack projects are then linked to a repository. The Bitbucket Cloud projet is not part of the configuration but will be displayed as an extra information.
