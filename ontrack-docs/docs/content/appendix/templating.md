# Templating

The templating engine is used to render some text (plain or in some markup language).

Each template is available to refer to a _context_, typically linked to an [event](../generated/events/index.md). These context items can be rendered directly, enriched through _source fields_, optionally configured, and finally _filtered_ for additional formatting.

The general format of a template is a string that contains expressions like:

```text
${expression}
```

Each expression is either a _function call_ or a _context reference_.

For a function call, the general syntax is:

```text
#.function?name1=value1&name2=value2|filter
```

!!! note

    A function can have any number of named configuration parameters or none at all like below:

    ```text
    #.function
    ```

For a context reference, the general syntax is similar:

```text
ref(.source)?name1=value1&name2=value2|filter
```

The `.source` is optional and allows to refine the context reference.

Examples of valid context references:

```text
project
branch.scmBranch|urlencode
promotionRun.changelog?acrossBranches=false
```

The list of context elements (project, branch, ...) depends on the execution context for the template.

For example, when using [notifications](../integrations/notifications/index.md), it all depends on the _event_ being subscribed to.

!!! note

    To see the list of possible events and their contexts, see the [reference](../generated/events/index.md).

The next sections list the available sources, functions, contexts and filters.

There are also special objects, known as _templating renderable_, which are specific to some contexts, like [auto-versioning](../integrations/auto-versioning/auto-versioning.md) or [workflows](../integrations/workflows/workflows.md).

* [sources](../generated/templating/sources/index.md)
* [functions](../generated/templating/functions/index.md)
* [filters](../generated/templating/filters/index.md)
* [renderables](../generated/templating/renderables/index.md)
