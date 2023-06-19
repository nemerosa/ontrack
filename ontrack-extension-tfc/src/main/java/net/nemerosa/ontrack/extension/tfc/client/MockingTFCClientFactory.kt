package net.nemerosa.ontrack.extension.tfc.client

import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.extension.tfc.config.TFCConfiguration
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
@Profile(RunProfile.ACC)
class MockingTFCClientFactory : TFCClientFactory {

    private val logger: Logger = LoggerFactory.getLogger(MockingTFCClientFactory::class.java)

    @PostConstruct
    fun logging() {
        logger.warn("Using a mocking TFC client")
    }

    override fun createClient(config: TFCConfiguration) = MockingTFCClient()

    inner class MockingTFCClient : TFCClient {

        override val organizations: List<TFCOrganization> = listOf(organization)

        override fun getWorkspaceVariables(workspaceId: String): List<TFCVariable> = listOf(
            TFCVariable(
                id = "var-1234",
                key = "ontrack_version",
                value = "4.5.3",
                sensitive = false,
                description = "Mock variable",
            )
        )

    }

    companion object {
        private val organization = TFCOrganization(
            id = "org-mock",
            name = "org-mock",
        )
    }
}