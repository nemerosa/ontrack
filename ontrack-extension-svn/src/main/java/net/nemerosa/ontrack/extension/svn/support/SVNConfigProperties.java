package net.nemerosa.ontrack.extension.svn.support;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Subversion application configuration
 */
@Data
@Component
@ConfigurationProperties(prefix = "ontrack.extension.svn")
@Deprecated
public class SVNConfigProperties {

    /**
     * Do we need to test the SVN URL for the repository configurations?
     */
    private boolean test = true;

}
