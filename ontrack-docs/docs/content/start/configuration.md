# Configuration

While Yontrack can be configured using its UI, it's recommended to use the CasC ([Configuration as Code](../appendix/casc.md)).

Find below different scenarios for configuring Yontrack using CasC.

## GitHub workflows

In this section, we'll see how to configure Yontrack for GitHub workflows.

> Don't forget to check the [Configuration as Code](../appendix/casc.md) section
> to see how to configure Yontrack using CasC.

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

In the_ Helm chart values_ for your Yontrack installation, declare this secret:

```yaml
ontrack:
  casc:
    secrets:
      mapping: file
      names:
        - yontrack-github
```

When Yontrack is restarted, it will be able to access the GitHub API. You can check this by navigating to your user menu at _Configurations_ > _GitHub configurations_. You should see the GitHub configuration you just created and you can test it by using the :octicons-question-16: button.

## Jenkins with GitHub

## Jenkins with Bitbucket Data Server
