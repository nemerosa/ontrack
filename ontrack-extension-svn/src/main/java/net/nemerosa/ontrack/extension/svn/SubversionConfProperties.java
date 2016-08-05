package net.nemerosa.ontrack.extension.svn;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for the SVN extension configuration properties.
 */
@Data
@Component
@ConfigurationProperties(prefix = "ontrack.extension.svn")
public class SubversionConfProperties {

    /**
     * Disabling the build sync jobs?
     */
    boolean buildSyncDisabled;

}
