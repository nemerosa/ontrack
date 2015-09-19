package net.nemerosa.ontrack.service.settings

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.GeneralSettings
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import static org.junit.Assert.assertEquals

class GeneralSettingsManagerIT extends AbstractServiceTestSupport {

    @Autowired
    private GeneralSettingsManager manager

    @Autowired
    private CachedSettingsService cachedSettingsService

    @Test
    void 'Saving the general settings'() {
        def settings = GeneralSettings.of()
                .withDisablingDuration(15)
                .withDeletingDuration(5)
        // Model test
        assert settings.disablingDuration == 15
        assert settings.deletingDuration == 5
        // Saving the settings
        asUser().with(GlobalSettings).call {
            manager.saveSettings(settings)
        }
        // Retrieving the settings
        def retrievedSettings = cachedSettingsService.getCachedSettings(GeneralSettings)
        // Test
        assertEquals(settings, retrievedSettings)
    }

}
