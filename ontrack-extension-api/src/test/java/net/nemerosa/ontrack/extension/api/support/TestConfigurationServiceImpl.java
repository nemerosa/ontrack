package net.nemerosa.ontrack.extension.api.support;

import net.nemerosa.ontrack.extension.support.AbstractConfigurationService;
import net.nemerosa.ontrack.model.events.EventFactory;
import net.nemerosa.ontrack.model.events.EventPostService;
import net.nemerosa.ontrack.model.security.EncryptionService;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.ConfigurationRepository;
import net.nemerosa.ontrack.model.support.ConnectionResult;
import net.nemerosa.ontrack.model.support.OntrackConfigProperties;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestConfigurationServiceImpl extends AbstractConfigurationService<TestConfiguration>
        implements TestConfigurationService {

    @NotNull
    @Override
    public String getType() {
        return "test";
    }

    @Autowired
    public TestConfigurationServiceImpl(ConfigurationRepository configurationRepository, SecurityService securityService, EncryptionService encryptionService, EventPostService eventPostService, EventFactory eventFactory, OntrackConfigProperties ontrackConfigProperties) {
        super(TestConfiguration.class, configurationRepository, securityService, encryptionService, eventPostService, eventFactory, ontrackConfigProperties);
    }

    @Override
    protected ConnectionResult validate(TestConfiguration configuration) {
        if (StringUtils.equals("check", configuration.getUser())) {
            if (StringUtils.equals("test", configuration.getPassword())) {
                return ConnectionResult.ok();
            } else {
                return ConnectionResult.error("Wrong password");
            }
        } else {
            return ConnectionResult.ok();
        }
    }
}
