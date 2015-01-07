package net.nemerosa.ontrack.extension.jira;

import lombok.Data;

import java.util.List;

/**
 * List of links to follow when displaying information about an issue.
 */
@Data
public class JIRAFollowLinksProperty {

    private final List<String> linkNames;

}
