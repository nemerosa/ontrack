package net.nemerosa.ontrack.extension.jira;

import net.nemerosa.ontrack.extension.jira.tx.JIRASession;
import net.nemerosa.ontrack.extension.jira.tx.JIRASessionFactory;
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
public class JIRAConfigurationServiceImpl extends AbstractConfigurationService<JIRAConfiguration> implements JIRAConfigurationService {

    private final JIRASessionFactory jiraSessionFactory;

    @Autowired
    public JIRAConfigurationServiceImpl(ConfigurationRepository configurationRepository, SecurityService securityService, EncryptionService encryptionService, EventPostService eventPostService, EventFactory eventFactory, JIRASessionFactory jiraSessionFactory, OntrackConfigProperties ontrackConfigProperties) {
        super(JIRAConfiguration.class, configurationRepository, securityService, encryptionService, eventPostService, eventFactory, ontrackConfigProperties);
        this.jiraSessionFactory = jiraSessionFactory;
    }

    @NotNull
    @Override
    public String getType() {
        return "jira";
    }

    @Override
    protected ConnectionResult validate(JIRAConfiguration configuration) {
        try (JIRASession jiraSession = jiraSessionFactory.create(configuration)) {
            // Gets the list of projects
            jiraSession.getClient().getProjects();
            // OK
            return ConnectionResult.ok();
        } catch (Exception ex) {
            return ConnectionResult.error(ex.getMessage());
        }
    }
}
