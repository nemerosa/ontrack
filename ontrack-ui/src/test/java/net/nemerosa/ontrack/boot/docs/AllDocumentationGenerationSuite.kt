package net.nemerosa.ontrack.boot.docs

import org.junit.jupiter.api.Disabled
import org.junit.platform.suite.api.ConfigurationParameter
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite

@Suite
@SelectClasses(
    value = [
        DocumentationGenerationIT::class,
        PropertiesDocumentationGenerationIT::class,
    ]
)
@ConfigurationParameter(
    key = "junit.jupiter.extensions.autodetection.enabled",
    value = "true"
)
@Disabled
class AllDocumentationGenerationSuite
