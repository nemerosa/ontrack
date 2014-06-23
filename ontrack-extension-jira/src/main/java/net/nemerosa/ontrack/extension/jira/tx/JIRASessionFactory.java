package net.nemerosa.ontrack.extension.jira.tx;

import net.nemerosa.ontrack.extension.jira.JIRAConfiguration;

public interface JIRASessionFactory {

    JIRASession create(JIRAConfiguration configuration);

}
