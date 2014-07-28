package net.nemerosa.ontrack.extension.github.model;

import lombok.Data;

@Data
public class GitHubMilestone {

    private final String title;
    private final GitHubState state;
    private final int number;
    private final String url;

}
