package net.nemerosa.ontrack.extension.jenkins;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class JenkinsJobProperty extends AbstractJenkinsProperty {

    /**
     * Name of the job.
     */
    private final String job;

    public JenkinsJobProperty(JenkinsConfiguration configuration, String job) {
        super(configuration);
        this.job = job;
    }

    /**
     * Derived property: the full URL to the Jenkins job.
     */
    public String getUrl() {
        return String.format("%s/jobs/%s", getConfiguration().getUrl(), job);
    }

}
