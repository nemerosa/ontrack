# Feeding Yontrack from GitHub Actions workflows

!!! note

    Make sure [Yontrack](../configuration/github.md) is configured for GitHub.

First, your GitHub workflows need to be able to connect to Yontrack.

In Yontrack, [create a token](../../security/tokens.md).

At your repository (or organization) level, create the following elements:

* a variable named `YONTRACK_URL` containing the URL of your Yontrack instance
* a secret named `YONTRACK_TOKEN` containing the token you created in the previous step

You're now ready to start feeding information into Yontrack from your GitHub workflows.

Start by creating a `.yontrack/ci.yaml` file at the root of your repository:

```yaml
version: v1
configuration: {}
```

!!! note

    This uses a default configuration for the items created in Yontrack.

    See [CI Configuration](../../configuration/ci-config.md) for more information.

In any workflow, you can use the following step:

```yaml
  - name: "Yontrack configuration"
    id: yontrack-config
    uses: nemerosa/ontrack-github-actions-cli-config@v{{ ontrack_github_actions_cli_config_version }}
    env:
      YONTRACK_URL: ${{ '{{' }} vars.YONTRACK_URL {{ '}}' }}
      YONTRACK_TOKEN: ${{ '{{' }} secrets.YONTRACK_TOKEN {{ '}}' }}
    with:
      github-token: ${{ '{{' }} secrets.GITHUB_TOKEN {{ '}}' }}
```

!!! note

    The `ontrack-github-actions-cli-config` action uses the [Yontrack CLI](https://github.com/nemerosa/ontrack-cli) to interact with Yontrack. The action will automatically download and configure the _latest_version of the CLI. That's why the `github-token` parameter is required in this case.

    If you want to use a specific version of the CLI, you can use the `version` parameter instead of `github-token`:

    ```yaml
    - name: "Yontrack configuration"
      id: yontrack-config
      uses: nemerosa/ontrack-github-actions-cli-config@v{{ ontrack_github_actions_cli_config_version }}
      env:
        YONTRACK_URL: ${{ '{{' }} vars.YONTRACK_URL {{ '}}' }}
        YONTRACK_TOKEN: ${{ '{{' }} secrets.YONTRACK_TOKEN {{ '}}' }}
      with:
        version: {{ ontrack_cli_version }}
    ```

This configures a project, branch and a build in Yontrack. The rest of the steps have now access to the [Yontrack CLI](https://github.com/nemerosa/ontrack-cli) to interact with Yontrack.

For example, to create a simple validation for the current build:

```yaml
- name: Validation
  run: |
    yontrack validate --validation BUILD --status PASSED
```

or better, with a condition on a previous step you want to validate:

{% raw %}
```yaml
- name: Some step
  id: some-step # The ID is needed to identity the step
  run: |
    # ... some steps ...
- name: Validation
  # Checking if the step has actually run at all
  if: ${{ steps.some-step.outcome != '' }}
  # Validation status depending on the outcome of the step
  run: |
    yontrack validate \
      --validation some-step \
      --status ${{ steps.some-step.outcome == 'success' && 'PASSED' || 'FAILED' }}
```
{% endraw %}