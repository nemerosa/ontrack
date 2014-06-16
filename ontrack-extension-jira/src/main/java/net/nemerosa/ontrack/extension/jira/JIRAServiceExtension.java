package net.nemerosa.ontrack.extension.jira;

import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration;
import net.nemerosa.ontrack.extension.issues.support.AbstractIssueServiceExtension;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

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

    @Override
    public Set<String> extractIssueKeysFromMessage(IssueServiceConfiguration issueServiceConfiguration, String message) {
        return extractJIRAIssuesFromMessage((JIRAConfiguration) issueServiceConfiguration, message);
    }

    protected Set<String> extractJIRAIssuesFromMessage(JIRAConfiguration configuration, String message) {
        Set<String> result = new HashSet<>();
        if (StringUtils.isNotBlank(message)) {
            Matcher matcher = JIRAConfiguration.ISSUE_PATTERN.matcher(message);
            while (matcher.find()) {
                // Gets the issue
                String issueKey = matcher.group();
                // Adds to the result
                if (configuration.isIssue(issueKey)) {
                    result.add(issueKey);
                }
            }
        }
        // OK
        return result;
    }
}
