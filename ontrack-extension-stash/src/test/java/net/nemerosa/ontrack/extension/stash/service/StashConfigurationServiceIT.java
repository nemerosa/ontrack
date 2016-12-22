package net.nemerosa.ontrack.extension.stash.service;

import net.nemerosa.ontrack.extension.stash.model.StashConfiguration;
import net.nemerosa.ontrack.it.AbstractServiceTestSupport;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.support.OntrackConfigProperties;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertFalse;

public class StashConfigurationServiceIT extends AbstractServiceTestSupport {

    @Autowired
    private StashConfigurationService configurationService;

    @Autowired
    private OntrackConfigProperties ontrackConfigProperties;

    /**
     * Regression test for #395
     */
    @Test
    public void deleteBitbucketOrg() throws Exception {
        boolean configurationTest = ontrackConfigProperties.isConfigurationTest();
        ontrackConfigProperties.setConfigurationTest(false);
        try {
            String confName = "bitbucket.org";
            asUser().with(GlobalSettings.class).call(() -> {
                configurationService.newConfiguration(
                        new StashConfiguration(
                                confName,
                                "https://bitbucket.org",
                                "", ""
                        )
                );
                configurationService.deleteConfiguration(confName);
                return null;
            });
            assertFalse(configurationService.getOptionalConfiguration(confName).isPresent());
        } finally {
            ontrackConfigProperties.setConfigurationTest(configurationTest);
        }
    }

}
