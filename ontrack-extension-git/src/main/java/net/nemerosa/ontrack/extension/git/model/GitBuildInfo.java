package net.nemerosa.ontrack.extension.git.model;

import lombok.Data;

@Data
public class GitBuildInfo {

    public static final GitBuildInfo INSTANCE = new GitBuildInfo();

    /**
     * This field is not used
     */
    private final String placeholder = "";

}
