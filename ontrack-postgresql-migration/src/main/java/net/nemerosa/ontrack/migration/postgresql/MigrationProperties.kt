package net.nemerosa.ontrack.migration.postgresql

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = "ontrack.migration")
@Component
class MigrationProperties {

    var isCleanup = false
    var isSkipEvents = false
    var isSkipBlobErrors = false
    var h2 = DatabaseProperties()
    var postgresql = DatabaseProperties()

    class DatabaseProperties {
        var url: String? = null
        var username: String? = null
        var password: String? = null
    }

}
