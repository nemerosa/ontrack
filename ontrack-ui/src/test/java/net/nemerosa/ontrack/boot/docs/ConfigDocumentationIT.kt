package net.nemerosa.ontrack.boot.docs

import net.nemerosa.ontrack.model.docs.DocumentationIgnore
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.boot.context.properties.ConfigurationProperties
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.jvmName

@Disabled("To be launched manually")
class ConfigDocumentationIT : AbstractDocumentationGenerationTestSupport() {

    @Test
    fun `Configuration properties`() {
        val configurations = findAllBeansAnnotatedWith(ConfigurationProperties::class)

        withDirectory("configurations") {
            configurations
                .filter {
                    it::class.jvmName.startsWith("net.nemerosa.ontrack")
                }
                .filterNot {
                    it::class.hasAnnotation<DocumentationIgnore>()
                }
                .forEach { configuration ->
                    generateConfiguration(this, configuration)
                }

        }
    }

    private fun generateConfiguration(
        directoryContext: DirectoryContext,
        configuration: Any
    ) {
        println("Generation docs for configuration $configuration")
    }

}