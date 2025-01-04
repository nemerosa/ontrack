package net.nemerosa.ontrack.common

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SystemPropertiesUtilsTest {

    @Test
    fun `Camel case to kebab case`() {
        assertEquals(
            "ontrack.config.key-store",
            "ontrack.config.keyStore".camelCaseToKebabCase(),
        )
    }

    @Test
    fun `Camel case to environment name`() {
        assertEquals(
            "ONTRACK_CONFIG_KEYSTORE",
            "ontrack.config.keyStore".camelCaseToEnvironmentName(),
        )
    }

    @Test
    fun `Camel case to environment name with wildcard`() {
        assertEquals(
            "ONTRACK_EXTENSION_QUEUE_SPECIFIC_<*>_SCALE",
            "ontrack.extension.queue.specific.<*>.scale".camelCaseToEnvironmentName(),
        )
    }

}