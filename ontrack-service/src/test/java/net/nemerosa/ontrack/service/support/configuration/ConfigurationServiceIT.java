package net.nemerosa.ontrack.service.support.configuration;

import net.nemerosa.ontrack.it.AbstractITTestSupport;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ConfigurationServiceIT extends AbstractITTestSupport {

    @Autowired
    private TestConfigurationService configurationService;

    @Test
    public void encryptedPassword() {

    }

}
