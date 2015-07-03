package net.nemerosa.ontrack.extension.svn.service

import net.nemerosa.ontrack.extension.svn.db.SVNRepositoryDao
import net.nemerosa.ontrack.extension.svn.model.SVNConfiguration
import net.nemerosa.ontrack.extension.svn.model.SVNURLFormatException
import net.nemerosa.ontrack.model.security.EncryptionService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.ConfigurationRepository
import org.junit.Before
import org.junit.Test

import static org.mockito.Mockito.mock

class SVNConfigurationServiceTest {

    SVNConfigurationServiceImpl configurationService

    @Before
    void 'Before'() {
        configurationService = new SVNConfigurationServiceImpl(
                mock(ConfigurationRepository),
                mock(SecurityService),
                mock(EncryptionService),
                mock(SVNRepositoryDao)
        )
    }

    @Test(expected = SVNURLFormatException)
    void 'No trailing slash in the URL'() {
        configurationService.validateConfiguration(
                SVNConfiguration.of("test", "svn://localhost/")
        )
    }

}
