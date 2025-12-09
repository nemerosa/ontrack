# Auto-versioning

Beside collecting data about the performance of your delivery, Yontrack can in turn use this information to drive other
automation processes.

One of these processes that Yontrack can drive is the "auto-versioning on promotion", which allows the propagation of
versions from one repository to others using quality gates based on
Yontrack [promotions](../../concepts/model/index.md#promotion-levels).

Let's imagine a project `parent` which has a dependency on a `module` expressed through a version property somewhere in
a file.

Ideally, whenever the `module` has a new version is a given range, we want this version to be used automatically by the
`parent`.

Manually, we can do this of course:

* We update the version in the `parent`
* We perform any needed post-processing like a resolution of locks
* We commit and push the change.
* Voilà.

If we put extra automation in the mix, you can define a perfectly valid auto-versioning process, and some external tools
can even be used to perform this process for you.

This becomes more complex whenever having a new version of the `module` is not enough of a criteria to have it used.
This may be a release which has not been qualified yet by extra quality processes (long running acceptance tests for
example, etc.).

That's where the concept of [promotion](../../concepts/model/index.md#promotion-levels) in Yontrack can play an
essential role:

* the `module` is promoted
* this starts the following process:
    * Yontrack creates a pull request for the `parent` where the version of the `module` has been changed to the one
      being
      promoted
    * any required post processing is performed on this PR
    * when the PR is ready to be merged (with all its controls), it's merged automatically

Result:

* versions are propagated automatically only when "promotion gates" are opened
    * one quality gate at the source, using promotions
    * one quality gate at the target, using automated checks

This is valid from one module to a project, and can be easily extended to a full tree of dependent modules.

The diagram below shows how this works:

![Auto-versioning overview](auto-versioning-overview.png)

!!! note "When not to use auto versioning"

    While auto-versioning is pretty easy to put in place, it should not be used where traditional dependency management
    based on locks can be used instead for simple code libraries.
    
    Auto-versioning on promotion is, however, particularly well suited to deal with situations like:
    
    * modular monoliths
    * GitOps repositories with fixed versions

## Configuration

The auto-versioning configurations are set at the level of the _target branches_. The exact way to send the
auto-versioning configuration depends on your type of [client](../../start/configuration.md), but we recommend
the [CI config injection](../../configuration/ci-config.md#auto-versioning).

See the [integrations](#integrations) after this section.

--8<-- "auto-versioning/config.md"

!!! note

    When an auto-versioning configuration is set, it can be checked in the branch page, in its properties.

### Targeting a series of branches

In this scenario, the parent wants to be notified of a promotion on a series of branches, and Yontrack triggers the
upgrade _only_ if the promotion has occurred on the _latest_ branch.

Setup:

* set the `sourceBranch` parameter to a regular expression on the Git branch, for example: `release\/.\..*`

How does it work?

* when a promotion occurs on the desired level:
    * Yontrack gets the list of branches for the dependency
    * orders them by descending version
    * filters them using the semantic versioning
    * triggers an upgrade only if the promoted branch is the first in this list (latest in terms of version)

Pro's:

* simple
* allows auto upgrades fairly easily

Con's:

* the dependency must really take care of a strong semantic versioning

### Branch expressions

The `sourceBranch` parameter can be set to `&<expression>` where `<expression>` is an expression used to detect the
source branch on the source project for a branch eligible for auto versioning.

Supported values are listed below.

#### `&regex`

By using:

```yaml
sourceBranch: "&regex:<regex>"
```

this is equivalent to the default behaviour:

```yaml
sourceBranch: "<regex>"
```

#### `&same`

The source branch must have the exact same name as the target branch.

Example: if you have a branch `release-1.24` on a parent project `P` and you want to get updates from a `dependency`
project only for the same branch, `release-1.24`, you can use:

```yaml
sourceBranch: "&same"
```

#### `&most-recent`

Two branches (`release/1.1` & `release/1.2`) are available for a project which is dependency of an auto-versioned parent
project with the following default branch source:

```yaml
branch: 'release\/1\..*'
```

In this scenario, no promotion has been granted yet in `release 1.2` of the dependency.

When 1.1 is promoted, Yontrack identifies a branch on the parent project to be a potential candidate for
auto-versioning.

This branch is configured to accept only the latest `release/1.*` branch, which is - now - the `release/1.2`.

Therefore, a 1.1 promotion is no longer eligible as soon as the 1.2 branch was created (and registered in Yontrack).

What exactly do we want to achieve? In this scenario, we always want the version promoted in 1.1 as long as there is
none in 1.2. Let's imagine we promote a 1.1 while 1.2 was already promoted, what then? How do we protect ourselves?

The idea is to accept a promotion as long as there is no such a promotion in later branches.

* a 1.1 is promoted, and there is no such promotion in more recent branches (1.2, etc.), we accept it
* a 1.1 is promoted, and there is already such a promotion in a more recent branch (1.2 for example), we reject it

To implement this strategy, we have to use:

```yaml
branch: '&most-recent:release\/1\..*'
```

#### `&same-release`

On the same model as the "&same" `sourceBranch` parameter, there is the possibility to get a "&same-release" branch
source.

This is to be used in cases where the dependency and its parent follow the same branch policy at `release/` branch
level, but only for a limited number of levels.

For example, a parent has release branches like release/1.24.10, with a dependency using on release/1.24.15. We want
release/1.x.y to always depend on the latest release/1.x.z branch (using 1. as a common prefix).

One way to do this is to use: `sourceBranch: "release/1.24.*"`  but this would force you to always update the source
branch parameter for every branch:

* release/1.24.* in release/1.24.x branch
* release/1.25.* in release/1.25.x branch
* etc.

A better way is to use, in this scenario:

```yaml
sourceBranch: "&same-release:2"
```

This means:

* if you're on a release/x.y.z branch, use release/x.y.* for the latest branch
* for any other branch (main) for example, we use the same branch

!!! note

    Note that `:2` means: take the first two numbers of the version of the release branch. By default, it'd be `:1` and can be omitted: `sourceBranch: "&same-release"`.

### Version source

By default, the version to use in the target project is computed directly from
the [build](../../concepts/model/index.md#builds) which has been promoted.

The default behavior is:

* if the source project is configured to use the labels for the
  builds (["Build name display"](../../generated/properties/property-net.nemerosa.ontrack.extension.general.BuildLinkDisplayPropertyType.md)
  property), the label (or release, or version) of the build is used. If this label is not present, the auto-versioning
  request will be rejected
* if the source project is not configured, the build name is taken as the version

This version computation can be adapted using the [`versionSource`](#configuration) configuration parameter.

The different options for this parameter are:

* `default` - uses the default behavior described above
* `name` - uses the name of the build, regardless of the source project configuration
* `labelOnly` - uses the label attached to the build, regardless of the source project configuration. If there is no
  label, the auto versioning request is rejected
* `metaInfo/<category>/<name>` or `metaInfo/<name>` - the version is the value of a meta-information item of the request
  category (optional) or name. If so such meta-information is found, the auto versioning request is rejected.

### Additional paths

The `additionalPaths` configuration property allows the specification of additional paths to update
instead of just the main one.

!!! note

    This can somehow be considered as a form of [post-processing](#post-processing) but
    without the need to call an external service.

Example:

```yaml
configurations:
  - # ...
    targetPath: "gradle.properties"
    targetProperty: "one-version"
    additionalPaths:
      - path: manifest.toml
        property: global.oneVersion
        propertyType: toml
        versionSource: metaInfo/rpmVersion
```

In this example, we want the auto-versioning to:

* update the `one-version` property of the `gradle.properties` file using the version of the build having been promoted
* update the `global.oneVersion` property of the `manifest.toml` file, but this time using the `rpmVersion`
  meta-information of the build having been promoted

Both changes will be part of the same PR.

[Post-processing](#post-processing) is still possible and would be run after all changes have been applied first (
default path & additional paths).

### Target files types

Auto)versioning, in the end, works by updating a _target file_, designed in the configuration by the `targetPath` or
`path` property.

A regular expression (`regex` parameter) can be used to identify the change.

This expression is used to 1) identify the current version 2) replace the current version by a new one.
In order for this to work, the regular expression must:

* match the whole target line in the target file
* have a capturing group in position 1 identifying the version to read or replace

It is also possible to use a higher level of file type, by specifying a _propertyName_ and optionally a _propertyType_.

The _propertyName_ designates a _property_ in the target file and the _propertyType_ designates the type of the file to
replace.

The following types are currently supported:

* `properties` (default) - Java properties file, typically used for a `gradle.properties` file
* `npm` - NPM package file, typically used for `package.json`
* [`maven`](#maven-pom-file) - Maven POM file
* [`yaml`](#yaml-files) - YAML file
* [`toml`](#toml-files) - TOML file

#### Maven POM file

For the `maven` type, the file to transform is a Maven `pom.xml` file. The `property` is _required_ to be one of the
`<properties>` elements of the file.

For example, given the following POM:

```xml

<project>
    <properties>
        <dep.version>1.10</dep.version>
        <yontrack.version>4.4.10</yontrack.version>
    </properties>
</project>
```

we can refer to the `yontrack.version` using the following auto versioning configuration:

```yaml
configurations:
  - # ...
    targetPath: pom.xml
    propertyType: maven
    property: yontrack.version
```

#### YAML files

When `propertyType` is set to `yaml`, `property` is expected to define a path inside the YAML file.

This path is expressed using
the https://docs.spring.io/spring/docs/4.3.25.RELEASE/spring-framework-reference/htmlsingle/#expressions[Spring
Expression Language].

For example, given the following YAML file (a deployment fragment in Kubernetes):

```yaml
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app
spec:
  template:
    spec:
      containers:
        - name: component
          image: repo/component:0.1.1
```

To get to the `repo/component:0.1.1` value, the path to set will be:

```text
#root.^[kind == 'Deployment' and metadata.name == 'my-app'].spec.template.spec.containers.^[name == 'component'].image
```

See
the [Spring Expression Language](https://docs.spring.io/spring/docs/4.3.25.RELEASE/spring-framework-reference/htmlsingle/#expressions)
reference for a complete reference but this expression already illustrates some key points:

* `#root` refers to the "root object", used to evaluate the expression, in our case, the list of YAML "documents",
  separated by `---`
* `.^[<filter>]` is an operator for a list, evaluating the given filter for each element until one element is found.
  Only the found element is returned.
* `.name` returns the value of the `name` property on an object
* literal strings are using single quotes, for example: `'Deployment'`

If `property` is set to the expression mentioned above, the value being returned will be
`repo/component:0.1.1`.

However, we want to use `0.1.1`only.

For this purpose, you need to specify also the `propertyRegex` and set it, for this example to:

```yaml
propertyRegex: '^repo\/component:(.*)$'
```

!!! note

    Putting regular expressions in YAML files can be tricky. One safe way is to use single-quotes to surround them.

!!! warning

    The use of SpringEL can be difficult to understand for non-Spring developers. There is a task
    in Yontrack 5 to support JSON path expressions. Please contact your support if you're interested in this feature.

#### TOML files

When `propertyType` is set to `toml`, `property` is expected to define a path inside the TOML file.

For example, given the following TOML file:

```toml
[images]
myVersion = "2.0.0"
```

To update the `myVersion` property in the `images` table, one can set the auto versioning `property` to
`images.myVersion`.

!!! warning

    As of the time of writing, the TOML support is still experimental. See
    issue [#1156](https://github.com/nemerosa/ontrack/issues/1156).

## Post-processing

In some cases, it's not enough to have only a version being updated into one file.
Some additional post-processing may be needed.

For example, if using Gradle or NPM dependency locks, after the version is updated, you'd need to resolve and write the new dependency locks.

The auto-versioning feature allows you to configure this post-processing.

In the [branch configuration](#configuration), you can set two properties for each source configuration:

* `postProcessing` - ID of the post-processing mechanism
* `postProcessingConfig` - configuration for the post-processing mechanism

As of now, only two post-processing mechanisms are supported:

* [Jenkins pipeline](jenkins.md)
* [GitHub Actions workflow](github.md)

## Integrations

While using the [CI config injection](../../configuration/ci-config.md#auto-versioning) for the configuration of the auto-versioning, there are several other ways to setup it.

### Jenkins pipeline

By using the [Jenkins Yontrack pipeline library](https://github.com/nemerosa/ontrack-jenkins-cli-pipeline), you can setup the auto versioning configuration for a branch.

For example:

```groovy
ontrackCliAutoVersioning {
    branch "main"
    yaml "auto-versioning.yaml"
}
```

where `auto-versioning.yaml` is a file in the repository containing for example:

```yaml
dependencies:
- project: my-library
  branch: release-1.3"
  promotion: IRON
  path: gradle.properties
  property: my-version
  postProcessing: jenkins
  postProcessingConfig:
  dockerImage  : openjdk:8
  dockerCommand: ./gradlew clean
```

!!! warning

    For historical reasons, this YAML file uses `dependencies` as a root instead of `configurations`.

## Examples

### Gradle update for last release

To automatically update the `dependencyVersion` in `gradle.properties` to the latest version
`1.*` of the project `dependency` when it is promoted to `GOLD`:

* sourceProject: `dependency`
* sourceBranch: `release/1\..*`
* sourcePromotion: `GOLD`
* targetPath: `gradle.properties`
* targetPropertyName: `dependencyVersion`
* targetPropertyType: `properties` (or nothing, it's a default)
* postProcessing: `...`
* postProcessingConfig:
    * `dockerImage`: `openjdk/8`
    * `dockerCommand`: `./gradlew resolveAndLockAll --write-locks`

### NPM update for last release

To automatically update the `@test/module` in `package.json` to the latest version
`1.*` of the project `dependency` when it is promoted to `GOLD`:

* sourceProject: `dependency`
* sourceBranch: `release/1\..*`
* sourcePromotion: `GOLD`
* targetPath: `package.json`
* targetPropertyName: `@test/module`
* targetPropertyType: `npm`
* postProcessing: `...`
* postProcessingConfig:
    * `dockerImage`: `node:jessie`
    * `dockerCommand`: `npm -i`

## Settings

### General configuration

Auto-versioning is enabled by default.

This can be disabled in the settings. Go to your user menu, in _System_ > _Settings_, then select _Auto-versioning_.

The following settings are available:

* _Enabled_ - enables or disables the auto-versioning in Yontrack
* _Audit retention duration_ - maximum duration to keep audit entries for active auto-versioning requests ()
* _Audit cleanup duration_ - maximum duration to keep audit entries for all kinds of auto-versioning requests (counted
  after the audit retention)
* _Build links_ - check to enable the creation of build links on auto-versioning (checked by default)

!!! note

    You can configure the settings as [code](../../configuration/casc.md):
    
    ```yaml
    ontrack:
      config:
        settings:
          auto-versioning:
            enabled: true
            auditRetentionDuration: 14d
            auditCleanupDuration: 90d
            buildLinks: true
    ```

### Queues

Yontrack uses queues in RabbitMQ to schedule and process auto-versioning events.

By default, `10` queues are allocated to process all auto-versioning events. You can
[monitor the queues in RabbitMQ](../../operations/rabbitmq.md) directly or use the
[auto-versioning metrics](../../operations/metrics.md) to know the load on the queues.

To change the number of queues, you can use
the [auto-versioning configuration properties](../../generated/configurations/net.nemerosa.ontrack.extension.av.AutoVersioningConfigProperties.md).
