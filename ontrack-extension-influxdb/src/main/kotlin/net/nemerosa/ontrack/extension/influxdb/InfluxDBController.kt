package net.nemerosa.ontrack.extension.influxdb

import net.nemerosa.ontrack.model.security.ApplicationManagement
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

/**
 * REST controller accessible from the UI.
 */
@ConditionalOnProperty(prefix = INFLUXDB_EXTENSION_PROPERTIES_PREFIX,
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = false)
@RestController
@RequestMapping("/extension/influxdb")
class InfluxDBController(
    private val influxDBExtensionHealthIndicator: InfluxDBExtensionHealthIndicator,
    private val influxDBConnection: InfluxDBConnection,
    private val securityService: SecurityService
) {

    /**
     * Gets the status of the current connection
     */
    @GetMapping("")
    fun getStatus(): Health {
        securityService.checkGlobalFunction(ApplicationManagement::class.java)
        return influxDBExtensionHealthIndicator.health()
    }

    /**
     * Resets the connection
     */
    @PostMapping("")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun reset() {
        securityService.checkGlobalFunction(ApplicationManagement::class.java)
        influxDBConnection.reset()
    }
}