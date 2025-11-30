# Configuration

While Yontrack can be configured using its UI, it's recommended to use the CasC ([Configuration as Code](../appendix/casc.md)).

Find below different scenarios for configuring Yontrack using CasC.

## GitHub workflows

In this section, we'll see how to configure Yontrack for GitHub workflows.

> Don't forget to check the [Configuration as Code](../appendix/casc.md) section
> to see how to configure Yontrack using CasC.

### Yontrack configuration

First, Yontrack needs to be able to access the GitHub API. Create the GitHub token with the following permissions:

* `repo`
* `read:user`, `user:email`

Create a secret in the same namespace as Yontrack with the name `yontrack-github` and with the value of the GitHub token in the `token` key.

In your _Yontrack Casc files_, define:

{% raw %}
```yaml
ontrack:
  config:
    github:
      name: github
      url: https://github.com
      oauth2Token: {{ secrets.yontrack-github.token }}
```
{% endraw %}

In the _Helm chart values_ for your Yontrack installation, declare this secret:

```yaml
ontrack:
  casc:
    secrets:
      mapping: file
      names:
        - yontrack-github
```

When Yontrack is restarted, it will be able to access the GitHub API. You can check this by navigating to your user menu at _Configurations_ > _GitHub configurations_. You should see the GitHub configuration you just created and you can test it by using the :octicons-question-16: button.

### Feeding information into Yontrack from GitHub workflows

First, your GitHub workflows need to be able to connect to Yontrack.

In Yontrack, [create a token](../security/tokens.md).

At your repository (or organization) level, create the following elements:

* a variable named `YONTRACK_URL` containing the URL of your Yontrack instance
* a secret named `YONTRACK_TOKEN` containing the token you created in the previous step

You're now ready to start feeding information into Yontrack from your GitHub workflows.

In any workflow, you can use the following steps:

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

## Jenkins with GitHub

## Jenkins with Bitbucket Data Server
