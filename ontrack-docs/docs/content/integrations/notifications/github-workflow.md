# GitHub workflows

Notifications can be used to trigger a GitHub Actions workflows on some events.

## Correlation ID

!!! note

    As of December 2025, the GitHub API does not provide a way to retrieve the run ID of a workflow run
    triggered by a workflow dispatch event.

As a workaround, a correlation ID is always passed as an input to the workflow with name `id`.

It's the responsibility of the workflow to use this `id` input and store it in a file which is then attached as an
artifact to the run.

For example:

```yaml
on:
  workflow_dispatch:
    inputs:
      id:
        description: "Correlation ID"
        required: true
        type: string
jobs:
  my-job:
    runs-on: ubuntu-latest
    steps:
      - name: logging
        run: touch inputs.properties
      - name: artifact
        uses: actions/upload-artifact@v4
        with:
          name: inputs-{{ '${{ inputs.id }}' }}.properties
          path: inputs.properties
          if-no-files-found: error
```

The code above is enough for Yontrack to retrieve the correlation ID and attach it to the run.

## See also

* [GitHub workflows configuration](../../generated/notifications/notification-backend-github-workflow.md)
