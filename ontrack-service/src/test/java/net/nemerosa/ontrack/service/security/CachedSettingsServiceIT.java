package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.it.AbstractServiceTestSupport;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.settings.CachedSettingsService;
import net.nemerosa.ontrack.model.settings.SecuritySettings;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

public class CachedSettingsServiceIT extends AbstractServiceTestSupport {

    @Autowired
    private CachedSettingsService settingsService;

    @Test
    public void cache_security_settings() throws Exception {
        asUser().with(GlobalSettings.class).call(() -> {
            // Gets the initial settings
            SecuritySettings s0 = settingsService.getCachedSettings(SecuritySettings.class);
            // Gets them a second time
            SecuritySettings s1 = settingsService.getCachedSettings(SecuritySettings.class);
            assertSame("The instance must have been cached", s0, s1);
            // Invalidates the cache
            settingsService.invalidate(SecuritySettings.class);
            // Gets this new version
            SecuritySettings s2 = settingsService.getCachedSettings(SecuritySettings.class);
            assertNotSame("The cached instance must have been discarded", s1, s2);
            // End
            return null;
        });
    }

}
