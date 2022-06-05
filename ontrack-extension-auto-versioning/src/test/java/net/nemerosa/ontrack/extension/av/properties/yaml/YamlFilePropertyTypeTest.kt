package net.nemerosa.ontrack.extension.av.properties.yaml

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertTrue

class YamlFilePropertyTypeTest {

    private val yaml = YamlFilePropertyType()

    @Test
    fun id() {
        assertEquals("yaml", yaml.id)
    }

    @Test
    fun readingVersion() {
        val value = yaml.readProperty(YAML, "[0].spec.source.targetRevision")
        assertEquals("1.0.0", value)
    }

    @Test
    fun setVersion() {
        val text = yaml.replaceProperty(YAML, "[0].spec.source.targetRevision", "1.1.1")
        assertTrue("1.1.1" in text)
    }

    @Test
    fun setVersionWithFormattedLines() {
        val text =
            yaml.replaceProperty(YAML_WITH_FORMATTED_LINES, "[0].spec.source.targetRevision", "2021.04.0-main-164")
        assertTrue("2021.04.0-main-164" in text, "Replacement has been done")
        assertEquals(
            AFTER_YAML_WITH_FORMATTED_LINES,
            text
        )
    }

    companion object {
        private val YAML = """
            apiVersion: argoproj.io/v1alpha1
            kind: Application
            metadata:
              name: test-listener
              namespace: argocd
            spec:
              destination:
                namespace: test-listener
                server: https://kubernetes.default.svc
              project: default
              source:
                repoURL: https://charts.sample.dev
                chart: edge-listener
                targetRevision: 1.0.0
              syncPolicy:
                automated:
                  prune: true
        """.trimIndent()

        private val YAML_WITH_FORMATTED_LINES = """
            metadata:
              finalizers:
              - "resources-finalizer.argocd.argoproj.io"
              name: "dgc"
            spec:
              destination:
                server: "https://sample"
                namespace: "dgc"
              source:
                path: ""
                repoURL: "https://repository.sample.io/artifactory/helm-gold/"
                targetRevision: "2021.04.0-main-103"
                chart: "dgc-aggregator"
                helm:
                  values: |-
                    dgc-core:
                      istio:
                        enabled: true
                      samplemicroservicechart:
                        namespaceLabels:
                          istio-injection: enabled
                        createNamespace: true
                        service:
                          annotations:
                            koncrete.v2.sample.com/network.sub-domain: dgc
                            koncrete.v2.sample.com/network.access.scope: SECURED
                        configMaps:
                          config:
                            data:
                              configuration-overrides.json: |-
                                {
                                    "cloud": {
                                        "logsEnabled": true
                                    },
                                    "general": {
                                      "baseUrl": "https://dgc.sandbox.cp.sample.dev/"
                                    }
                                }
                        resources:
                          requests:
                            cpu: 2000m
                            memory: 8192Mi
                          limits:
                            cpu: 4000m
                            memory: 8192Mi
              project: "applications"
              syncPolicy:
                automated:
                  prune: true
                  selfHeal: true
        """.trimIndent()

        private val AFTER_YAML_WITH_FORMATTED_LINES = """
            ---
            metadata:
              finalizers:
              - "resources-finalizer.argocd.argoproj.io"
              name: "dgc"
            spec:
              destination:
                server: "https://C9DA8DF0F962F8018AAC10EFED012223.gr7.eu-west-1.eks.amazonaws.com"
                namespace: "dgc"
              source:
                path: ""
                repoURL: "https://repository.sample.io/artifactory/helm-gold/"
                targetRevision: "2021.04.0-main-164"
                chart: "dgc-aggregator"
                helm:
                  values: |-
                    dgc-core:
                      istio:
                        enabled: true
                      samplemicroservicechart:
                        namespaceLabels:
                          istio-injection: enabled
                        createNamespace: true
                        service:
                          annotations:
                            koncrete.v2.sample.com/network.sub-domain: dgc
                            koncrete.v2.sample.com/network.access.scope: SECURED
                        configMaps:
                          config:
                            data:
                              configuration-overrides.json: |-
                                {
                                    "cloud": {
                                        "logsEnabled": true
                                    },
                                    "general": {
                                      "baseUrl": "https://dgc.sandbox.cp.sample.dev/"
                                    }
                                }
                        resources:
                          requests:
                            cpu: 2000m
                            memory: 8192Mi
                          limits:
                            cpu: 4000m
                            memory: 8192Mi
              project: "applications"
              syncPolicy:
                automated:
                  prune: true
                  selfHeal: true
            
        """.trimIndent()
    }

}