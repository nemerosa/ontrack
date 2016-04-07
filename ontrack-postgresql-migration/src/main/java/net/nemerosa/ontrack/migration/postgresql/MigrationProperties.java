package net.nemerosa.ontrack.migration.postgresql;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "ontrack.migration")
@Component
@Data
public class MigrationProperties {

    private DatabaseProperties h2 = new DatabaseProperties(
            "jdbc:h2:./work/ontrack/db/data;MODE=MYSQL;DB_CLOSE_ON_EXIT=FALSE;DEFRAG_ALWAYS=TRUE",
            "ontrack",
            "ontrack"
    );
    private DatabaseProperties postgresql = new DatabaseProperties(
            "jdbc:postgresql://postgresql/ontrack",
            "ontrack",
            "ontrack"
    );

    @Data
    @AllArgsConstructor
    public static class DatabaseProperties {
        private String url;
        private String username;
        private String password;

        public DatabaseProperties() {
        }
    }

}
