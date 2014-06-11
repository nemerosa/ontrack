package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.it.AbstractITTestSupport;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.settings.SecuritySettings;
import net.nemerosa.ontrack.model.settings.SettingsService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

public class SettingsServiceIT extends AbstractITTestSupport {

    @Autowired
    private SettingsService settingsService;

    @Test
    public void cache_security_settings() throws Exception {
        asUser().with(GlobalSettings.class).call(() -> {
            // Gets the initial settings
            SecuritySettings s0 = settingsService.getSecuritySettings();
            // Gets them a second time
            SecuritySettings s1 = settingsService.getSecuritySettings();
            assertSame("The instance must have been cached", s0, s1);
            // Saves a new version
            settingsService.saveSecuritySettings(SecuritySettings.of());
            // Gets this new version
            SecuritySettings s2 = settingsService.getSecuritySettings();
            assertNotSame("The cached instance must have been discarded", s1, s2);
            // End
            return null;
        });
    }

}
