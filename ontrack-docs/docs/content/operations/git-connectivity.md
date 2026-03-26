# Git connectivity

If Yontrack has difficulties connecting to your Git repositories because the latter is unstable (connectivity issues,
HTTP errors, etc.), it may be necessary to tune how Yontrack retries connections on errors.

By default, when Yontrack has a connectivity issue or receives an HTTP 5xx error, it'll retry 3 times every 30 seconds.

This basic behaviour can be adapted globally by setting the following [configuration properties](../generated/configurations/net.nemerosa.ontrack.extension.git.GitConfigProperties.md):

* `ONTRACK_CONFIG_EXTENSION_GIT_REMOTE_INTERVAL`
* `ONTRACK_CONFIG_EXTENSION_GIT_REMOTE_RETRIES`

However, this configuration is very static (Yontrack needs to be restarted to apply the changes) and not very flexible (no way to add specific rules).

Using [CasC](../configuration/casc.md), you can define a more complete set of rules.

For example, the default rules exposed above look like:

```yaml
ontrack:
  extensions:
    git:
      retryConfiguration:
        retries:
          - httpCode: "5[\\d]{2}"
            connectionError: true
            retryLimit: 3
            retryInterval: 30s
```

More complex rules can look like:

```yaml
ontrack:
  extensions:
    git:
        retryConfiguration:
          - connectionError: true
            retryLimit: 120
          - httpCode: "5[0-9]{2}"
          - httpCode: "409"
            errorMessage: ".*Rule was unable to be completed.*"
            retryLimit: 2
            retryInterval: 120s
```

Properties for each rule are:

* `httpCode` - HTTP code to match (regular expression). If empty, no match on the HTTP code.
* `errorMessage` - Error message to match (regular expression). Defaults to all messages.
* `connectionError` - If true, matches connection errors
* `retryLimit` - Specific retry limit. If empty, uses the default retry limit
* `retryInterval` - Retry interval. If empty, uses the default retry interval.
