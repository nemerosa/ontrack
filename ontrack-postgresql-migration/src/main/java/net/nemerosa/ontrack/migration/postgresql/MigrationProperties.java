package net.nemerosa.ontrack.migration.postgresql;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "ontrack.migration")
@Component
@Data
public class MigrationProperties {

    private boolean cleanup = false;
    private boolean skipEvents = false;
    private boolean skipBlobErrors = false;
    private DatabaseProperties h2 = new DatabaseProperties();
    private DatabaseProperties postgresql = new DatabaseProperties();

    @Data
    public static class DatabaseProperties {
        private String url;
        private String username;
        private String password;
    }

}
