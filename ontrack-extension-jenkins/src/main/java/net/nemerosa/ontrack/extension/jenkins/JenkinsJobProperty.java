package net.nemerosa.ontrack.extension.jenkins;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

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
        return String.format("%s/job/%s", getConfiguration().getUrl(), withFolders(job));
    }

    /**
     * Two pass replacement /job/ --> / --> /job/ in order to preserve the use of /job/
     */
    private String withFolders(String path) {
        return StringUtils.replace(
                StringUtils.replace(
                        path,
                        "/job/",
                        "/"
                ),
                "/",
                "/job/"
        );
    }

}
