package net.nemerosa.ontrack.extension.git;

import lombok.Data;

@Data
public class GitBranchesTemplateSynchronisationSourceConfig {

    private final String includes;
    private final String excludes;

}
