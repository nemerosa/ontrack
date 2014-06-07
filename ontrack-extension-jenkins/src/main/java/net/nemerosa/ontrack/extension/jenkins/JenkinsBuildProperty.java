package net.nemerosa.ontrack.extension.jenkins;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This property associates a build number with a Jenkins job.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class JenkinsBuildProperty extends JenkinsJobProperty {

    private final int build;

    public JenkinsBuildProperty(JenkinsConfiguration configuration, String job, int build) {
        super(configuration, job);
        this.build = build;
    }
}
