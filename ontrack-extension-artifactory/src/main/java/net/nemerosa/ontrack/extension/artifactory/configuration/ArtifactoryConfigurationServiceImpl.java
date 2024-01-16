package net.nemerosa.ontrack.extension.artifactory.configuration;

import net.nemerosa.ontrack.extension.artifactory.client.ArtifactoryClient;
import net.nemerosa.ontrack.extension.artifactory.client.ArtifactoryClientFactory;
import net.nemerosa.ontrack.extension.support.AbstractConfigurationService;
import net.nemerosa.ontrack.model.events.EventFactory;
import net.nemerosa.ontrack.model.events.EventPostService;
import net.nemerosa.ontrack.model.security.EncryptionService;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.ConfigurationRepository;
import net.nemerosa.ontrack.model.support.ConnectionResult;
import net.nemerosa.ontrack.model.support.OntrackConfigProperties;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ArtifactoryConfigurationServiceImpl extends AbstractConfigurationService<ArtifactoryConfiguration> implements ArtifactoryConfigurationService {

    private final ArtifactoryClientFactory artifactoryClientFactory;

    @Autowired
    public ArtifactoryConfigurationServiceImpl(ConfigurationRepository configurationRepository, SecurityService securityService, EncryptionService encryptionService, EventPostService eventPostService, EventFactory eventFactory, ArtifactoryClientFactory artifactoryClientFactory, OntrackConfigProperties ontrackConfigProperties) {
        super(ArtifactoryConfiguration.class, configurationRepository, securityService, encryptionService, eventPostService, eventFactory, ontrackConfigProperties);
        this.artifactoryClientFactory = artifactoryClientFactory;
    }

    @NotNull
    @Override
    public String getType() {
        return "artifactory";
    }

    @Override
    protected ConnectionResult validate(ArtifactoryConfiguration configuration) {
        try {
            ArtifactoryClient client = artifactoryClientFactory.getClient(configuration);
            // Gets the basic info
            client.getBuildNames();
            // OK
            return ConnectionResult.ok();
        } catch (Exception ex) {
            return ConnectionResult.error(ex.getMessage());
        }
    }
}
