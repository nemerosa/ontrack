package net.nemerosa.ontrack.extension.stash.service;

import net.nemerosa.ontrack.client.OTHttpClient;
import net.nemerosa.ontrack.extension.stash.model.StashConfiguration;
import net.nemerosa.ontrack.extension.support.AbstractConfigurationService;
import net.nemerosa.ontrack.extension.support.client.ClientConnection;
import net.nemerosa.ontrack.extension.support.client.ClientFactory;
import net.nemerosa.ontrack.model.events.EventFactory;
import net.nemerosa.ontrack.model.events.EventPostService;
import net.nemerosa.ontrack.model.security.EncryptionService;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.ConfigurationRepository;
import net.nemerosa.ontrack.model.support.ConnectionResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StashConfigurationServiceImpl extends AbstractConfigurationService<StashConfiguration> implements StashConfigurationService {

    private final ClientFactory clientFactory;

    @Autowired
    public StashConfigurationServiceImpl(ConfigurationRepository configurationRepository, SecurityService securityService, EncryptionService encryptionService, EventPostService eventPostService, EventFactory eventFactory, ClientFactory clientFactory) {
        super(StashConfiguration.class, configurationRepository, securityService, encryptionService, eventPostService, eventFactory);
        this.clientFactory = clientFactory;
    }

    @Override
    protected ConnectionResult validate(StashConfiguration configuration) {
        try {
            OTHttpClient client = clientFactory.getHttpClient(
                    new ClientConnection(
                            configuration.getUrl(),
                            configuration.getUser(),
                            configuration.getPassword()
                    )
            );
            if (client.get(content -> true, "projects")) {
                return ConnectionResult.ok();
            } else {
                return ConnectionResult.error("Cannot get the content of the Stash home page");
            }
        } catch (Exception ex) {
            return ConnectionResult.error(ex.getMessage());
        }
    }
}
