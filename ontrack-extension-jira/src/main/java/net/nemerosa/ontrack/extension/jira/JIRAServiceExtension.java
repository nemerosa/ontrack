package net.nemerosa.ontrack.extension.jira;

import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration;
import net.nemerosa.ontrack.extension.issues.support.AbstractIssueServiceExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JIRAServiceExtension extends AbstractIssueServiceExtension {

    public static final String SERVICE = "jira";
    private final JIRAConfigurationService jiraConfigurationService;

    @Autowired
    public JIRAServiceExtension(JIRAExtensionFeature extensionFeature, JIRAConfigurationService jiraConfigurationService) {
        super(extensionFeature, SERVICE, "JIRA");
        this.jiraConfigurationService = jiraConfigurationService;
    }

    @Override
    public List<? extends IssueServiceConfiguration> getConfigurationList() {
        return jiraConfigurationService.getConfigurations();
    }

    @Override
    public IssueServiceConfiguration getConfigurationByName(String name) {
        return jiraConfigurationService.getOptionalConfiguration(name).orElse(null);
    }
}
