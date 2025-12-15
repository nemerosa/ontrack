package net.nemerosa.ontrack.extension.av.properties.yaml.path

import net.nemerosa.ontrack.extension.av.processing.AutoVersioningMissingTargetPropertyException
import net.nemerosa.ontrack.extension.av.processing.AutoVersioningReadVersionException
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class YamlPathFilePropertyTypeTest {

    private val type = YamlPathFilePropertyType()

    @Test
    fun `Missing target property`() {
        assertFailsWith<AutoVersioningMissingTargetPropertyException> {
            type.readProperty(yamlContent, null)
        }
        assertFailsWith<AutoVersioningMissingTargetPropertyException> {
            type.readProperty(yamlContent, " ")
        }
    }

    @Test
    fun `Reading with implicit root path`() {
        // JsonPath accepts both $.metadata.name and metadata.name formats
        val property = type.readProperty(yamlContent, "metadata.name")
        assertEquals("yontrack", property)
    }

    @Test
    fun `Reading a simple property`() {
        val property = type.readProperty(yamlContent, "$.metadata.name")
        assertEquals("yontrack", property)
    }

    @Test
    fun `Reading a property in an array`() {
        val property = type.readProperty(yamlContent, "$.spec.sources[1].targetRevision")
        assertEquals("1.0.2", property)
    }

    @Test
    fun `Reading array element by index`() {
        // Test accessing array elements with specific index
        val property = type.readProperty(yamlContent, "$.spec.sources[0].repoURL")
        assertEquals("https://github.com/yontrack/yontrack", property)
    }

    @Test
    fun `Reading nested property with JsonPath`() {
        val property = type.readProperty(yamlContent, "$.spec.project")
        assertEquals("yontrack-production-eu", property)
    }

    @Test
    fun `Invalid JsonPath expression`() {
        assertFailsWith<AutoVersioningReadVersionException> {
            type.readProperty(yamlContent, "$.nonexistent.path")
        }
    }

    @Test
    fun `Replace property in array element`() {
        val newVersion = "2.0.0"
        val result = type.replaceProperty(yamlContent, "$.spec.sources[1].targetRevision", newVersion)

        // Verify the replacement worked by reading it back
        val readBack = type.readProperty(result, "$.spec.sources[1].targetRevision")
        assertEquals(newVersion, readBack)

        // Verify other properties remain unchanged
        assertEquals("yontrack", type.readProperty(result, "$.metadata.name"))
        assertEquals("main", type.readProperty(result, "$.spec.sources[0].targetRevision"))
    }

    @Test
    fun `Replace simple property`() {
        val newName = "new-yontrack"
        val result = type.replaceProperty(yamlContent, "$.metadata.name", newName)

        // Verify the replacement
        val readBack = type.readProperty(result, "$.metadata.name")
        assertEquals(newName, readBack)

        // Verify other properties remain unchanged
        assertEquals("1.0.2", type.readProperty(result, "$.spec.sources[1].targetRevision"))
    }

    @Test
    fun `Replace nested property`() {
        val newProject = "yontrack-staging"
        val result = type.replaceProperty(yamlContent, "$.spec.project", newProject)

        // Verify the replacement
        val readBack = type.readProperty(result, "$.spec.project")
        assertEquals(newProject, readBack)
    }

    @Test
    fun `Replace property with implicit root path`() {
        val newNamespace = "production"
        val result = type.replaceProperty(yamlContent, "metadata.namespace", newNamespace)

        // Verify the replacement
        val readBack = type.readProperty(result, "$.metadata.namespace")
        assertEquals(newNamespace, readBack)
    }

    @Test
    fun `Replace missing target property throws exception`() {
        assertFailsWith<AutoVersioningMissingTargetPropertyException> {
            type.replaceProperty(yamlContent, null, "1.0.0")
        }
        assertFailsWith<AutoVersioningMissingTargetPropertyException> {
            type.replaceProperty(yamlContent, " ", "1.0.0")
        }
    }

    companion object {
        private val yamlContent = """
            apiVersion: argoproj.io/v1alpha1
            kind: Application
            metadata:
              name: yontrack
              namespace: argocd
            spec:
              project: yontrack-production-eu
              sources:
                - repoURL: https://github.com/yontrack/yontrack
                  targetRevision: main
                  ref: values
                - repoURL: registry/yontrack
                  chart: yontrack-saas
                  targetRevision: 1.0.2
        """.trimIndent()
    }

}