package net.nemerosa.ontrack.extension.indicators.ui.graphql

import net.nemerosa.ontrack.extension.indicators.AbstractIndicatorsTestSupport
import net.nemerosa.ontrack.json.getRequiredBooleanField
import net.nemerosa.ontrack.model.security.Roles
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GQLRootQueryIndicatorsManagementIT : AbstractIndicatorsTestSupport() {

    @Test
    fun `Management menu containing all entries for an administrator`() {
        asGlobalRole(Roles.GLOBAL_ADMINISTRATOR) {
            run(
                """
              {
                indicatorsManagement {
                    portfolios
                    configuration
                    categories
                    types
                    views
                }
              }
            """
            ).let { data ->
                val links = data.path("indicatorsManagement")
                assertTrue(links.getRequiredBooleanField("portfolios"), "Portfolios are granted")
                assertTrue(links.getRequiredBooleanField("configuration"), "Indicators configuration is granted")
                assertTrue(links.getRequiredBooleanField("categories"), "Categories are granted")
                assertTrue(links.getRequiredBooleanField("types"), "Types are granted")
                assertTrue(links.getRequiredBooleanField("views"), "Views are granted")
            }
        }
    }

    @Test
    fun `Management menu containing only one entry for a non administrator`() {
        asGlobalRole(Roles.GLOBAL_READ_ONLY) {
            run(
                """
              {
                indicatorsManagement {
                    portfolios
                    configuration
                    categories
                    types
                    views
                }
              }
            """
            ).let { data ->
                val links = data.path("indicatorsManagement")
                assertTrue(links.getRequiredBooleanField("portfolios"), "Portfolios are granted")
                assertFalse(links.getRequiredBooleanField("configuration"), "Indicators configuration is not granted")
                assertFalse(links.getRequiredBooleanField("categories"), "Categories are not granted")
                assertFalse(links.getRequiredBooleanField("types"), "Types are not granted")
                assertFalse(links.getRequiredBooleanField("views"), "Views are not granted")
            }
        }
    }

}