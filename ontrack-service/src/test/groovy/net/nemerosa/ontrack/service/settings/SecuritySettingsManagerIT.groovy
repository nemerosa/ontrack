package net.nemerosa.ontrack.service.settings

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.SecuritySettings
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import static org.junit.Assert.assertEquals

class SecuritySettingsManagerIT extends AbstractServiceTestSupport {

    @Autowired
    private SecuritySettingsManager manager

    @Autowired
    private CachedSettingsService cachedSettingsService

    @Test
    void 'Saving the general settings'() {
        def settings = SecuritySettings.of()
        assert settings.grantProjectViewToAll
        settings = settings.withGrantProjectViewToAll(false)
        // Model test
        assert !settings.grantProjectViewToAll
        // Saving the settings
        asUser().with(GlobalSettings).call {
            manager.saveSettings(settings)
        }
        // Retrieving the settings
        def retrievedSettings = cachedSettingsService.getCachedSettings(SecuritySettings)
        // Test
        assertEquals(settings, retrievedSettings)
    }

}
