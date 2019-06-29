package net.nemerosa.ontrack.extension.svn

import net.nemerosa.ontrack.extension.support.ConfigurationConnectorStatusIndicator
import net.nemerosa.ontrack.extension.svn.client.SVNClient
import net.nemerosa.ontrack.extension.svn.model.SVNConfiguration
import net.nemerosa.ontrack.extension.svn.service.SVNService
import net.nemerosa.ontrack.extension.svn.support.SVNUtils
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.ConfigurationService
import net.nemerosa.ontrack.model.support.ConnectorDescription
import net.nemerosa.ontrack.tx.TransactionService
import org.springframework.stereotype.Component

@Component
class SVNConnectorStatusIndicator(
        configurationService: ConfigurationService<SVNConfiguration>,
        securityService: SecurityService,
        private val svnService: SVNService,
        private val transactionService: TransactionService,
        private val svnClient: SVNClient
) : ConfigurationConnectorStatusIndicator<SVNConfiguration>(configurationService, securityService) {

    /**
     * Gets the latest revision
     */
    override fun connect(config: SVNConfiguration) {// Just gets the latest revision
        val repository = svnService.getRepository(config.name)
        transactionService.start().use { _ ->
            val url = SVNUtils.toURL(repository.configuration.url)
            svnClient.getRepositoryRevision(repository, url)
        }
    }

    override fun connectorDescription(config: SVNConfiguration) = ConnectorDescription(
            type = "svn",
            name = config.name,
            connection = config.url
    )
}
