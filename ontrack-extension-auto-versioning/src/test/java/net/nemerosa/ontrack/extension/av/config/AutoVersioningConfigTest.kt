package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.extension.av.AutoVersioningTestFixtures.sourceConfig
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AutoVersioningConfigTest {

    @Test
    fun `Checking duplicates OK`() {
        AutoVersioningConfig(
            listOf(
                sourceConfig(sourceProject = "P1"),
                sourceConfig(sourceProject = "P2")
            )
        ).validate()
    }

    @Test
    fun `Checking duplicates NOT OK`() {
        val ex = assertFailsWith<AutoVersioningConfigDuplicateProjectException> {
            AutoVersioningConfig(
                listOf(
                    sourceConfig(sourceProject = "P1"),
                    sourceConfig(sourceProject = "P1"),
                    sourceConfig(sourceProject = "P2"),
                    sourceConfig(sourceProject = "P3"),
                    sourceConfig(sourceProject = "P3")
                )
            ).validate()
        }
        assertEquals(
            "It is not possible to configure a source project multiple times. Duplicate projects are: P1, P3",
            ex.message
        )
    }

}
