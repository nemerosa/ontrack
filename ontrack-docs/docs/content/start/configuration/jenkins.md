# Configuring Yontrack for Jenkins

In this section, we'll see how to configure Yontrack for Jenkins pipelines.

> Don't forget to check the [Configuration as Code](../../configuration/casc.md) section
> to see how to configure Yontrack using CasC.

Yontrack needs to be able to access the Jenkins API. Start by creating a Jenkins API token.

Create a secret in the same namespace as Yontrack with the name `yontrack-jenkins` and with the value of the Jenkins API token
in the `token` key.

In your _Yontrack Casc files_, define:

{% raw %}
```yaml
ontrack:
  config:
    jenkins:
      name: jenkins
      url: <jenkins URL>
      username: <Jenkins user email>
      password: {{ secrets.yontrack-jenkins.token }}
```
{% endraw %}

In the _Helm chart values_ for your Yontrack installation, declare this secret:

```yaml
ontrack:
  casc:
    secrets:
      mapping: file
      names:
        - yontrack-jenkins
```

When Yontrack is restarted, it will be able to access the Jenkins API. You can check this by navigating to your user menu
at _Configurations_ > _Jenkins configurations_. You should see the Jenkins configuration you just created and you can test
it by using the :octicons-question-16: button.
