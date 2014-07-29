package net.nemerosa.ontrack.extension.svn.property;

import lombok.Data;

import java.util.List;

@Data
public class SVNRevisionChangeLogIssueValidatorConfig {

    private final List<String> closedStatuses;

}
