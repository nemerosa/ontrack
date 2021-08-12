package net.nemerosa.ontrack.extension.indicators.ui.graphql

import net.nemerosa.ontrack.extension.indicators.AbstractIndicatorsTestSupport
import net.nemerosa.ontrack.model.security.Roles
import org.junit.Test

class GQLRootQueryIndicatorsManagement: AbstractIndicatorsTestSupport() {

    @Test
    fun `Management menu containing all entries for an administrator`() {
        asGlobalRole(Roles.GLOBAL_ADMINISTRATOR) {

        }
    }

}