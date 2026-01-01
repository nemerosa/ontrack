# Environments

!!! warning

    This feature is under license.

!!! note

    Documentation is in progress.

## Configuration

While environments and their slots can be configured through the UI, it's recommenced to use either:

* [CI configuration](#ci-configuration)
* [CasC](#casc)

### CI configuration

Using the [CI config](../../configuration/ci-config.md) feature, one can define the environments and slots linked to a
project directly from its Yontrack CI config file.

For example:

```yaml
version: v1
configuration:
  defaults:
    project:
      environments:
        environments:
          - name: self.yontrack.com
            description: Production environment for Yontrack itself
            order: 200
            tags:
              - yontrack
              - release
        slots:
          - project: yontrack
            environments:
              - name: self.yontrack.com
                admissionRules:
                  - ruleId: promotion
                    ruleConfig:
                      promotion: GOLD
                  - ruleId: branchPattern
                    ruleConfig:
                      includes:
                        - main
                workflows:
                  - name: Creation
                    trigger: CANDIDATE
                    nodes:
                      - id: start
                        executorId: mock
                        data:
                          text: Start
                      - id: end
                        parents:
                          - id: start
                        executorId: mock
                        data:
                          text: End
```

In this case, unless in the [CasC setup](#casc) where this behavior is configurable, the environments are not
authoritative. Only the slots are for the configured project.

A typical use case for this CI config of environments would be done at the level where the actual environment is
actually deployed, like in a GitOps repository.

### CasC

TBD
