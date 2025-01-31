package net.nemerosa.ontrack.kdsl.acceptance.tests

import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.connector.support.DefaultConnector
import java.util.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation

object ACCProperties {

    object Connection {
        @DefaultValue("http://localhost:8080")
        val url: String by fromEnv()
        val token: String by lazy {
            obtainToken()
        }

        @DefaultValue("admin")
        val username: String? by fromEnv()

        @DefaultValue("admin")
        val password: String? by fromEnv()

        object Mgt {
            @DefaultValue("http://localhost:8800/manage")
            val url: String by fromEnv()
        }

        object Internal {
            @DefaultValue("http://localhost:8080")
            val url: String by fromEnv()
        }

    }

    object AutoVersioning {
        @DefaultValue("120000") // 2 minutes
        val autoVersioningCompletion: Long by longFromEnv()
    }

    object MockSCM {
        object Timeouts {
            @DefaultValue("30000")
            val general: Long by longFromEnv()
            @DefaultValue("500")
            val interval: Long by longFromEnv()
        }
    }

    object GitHub {
        val token: String? by optionalFromEnv()
    }

    object InfluxDB {
        @DefaultValue("http://localhost:8086")
        val url: String by fromEnv()
    }

    @Target(AnnotationTarget.PROPERTY)
    @Retention(AnnotationRetention.RUNTIME)
    annotation class DefaultValue(
        val value: String = NONE,
    ) {
        companion object {
            const val NONE = "<NONE>"
        }
    }

    private fun obtainToken(): String {
        val providedToken = optionalFromEnv().getValue(Connection, Connection::token)
        return if (providedToken != null) {
            providedToken
        } else {
            val url = Connection.url
            val username = Connection.username ?: error("Username is required to get a token")
            val password = Connection.username ?: error("Password is required to get a token")
            getOrCreateToken(url, username, password)
        }
    }

    fun getOrCreateToken(url: String, username: String, password: String): String {
        // Unique name for the token
        val tokenName = uid("acc_")
        // Basic authentication
        val basic: String = "$username:$password".run {
            Base64.getEncoder().encodeToString(toByteArray(Charsets.UTF_8))
        }
        // Creating a connector for this URl and these credentials
        val connector = DefaultConnector(
            url = url,
            defaultHeaders = mapOf(
                "Authorization" to "Basic $basic"
            )
        )
        return connector.post("/rest/tokens/create/${tokenName}")
            .apply {
                if (statusCode != 200) {
                    error("Cannot get a new token")
                }
            }
            .body.asJson()
            .path("token")
            .path("value")
            .asText()
    }

    private fun fromEnv(): ReadOnlyProperty<Any, String> =
        ReadOnlyProperty { thisRef, property ->
            optionalFromEnv().getValue(thisRef, property)
                ?: error("No system property not environment variable found for $property in $thisRef.")
        }

    private fun longFromEnv(): ReadOnlyProperty<Any, Long> =
        ReadOnlyProperty { thisRef, property ->
            optionalLongFromEnv().getValue(thisRef, property)
                ?: error("No system property not environment variable found for $property in $thisRef.")
        }

    private fun optionalFromEnv(): ReadOnlyProperty<Any, String?> =
        ReadOnlyProperty { thisRef, property ->
            getValue(thisRef, property) {
                it
            }
        }

    private fun optionalLongFromEnv(): ReadOnlyProperty<Any, Long?> =
        ReadOnlyProperty { thisRef, property ->
            getValue(thisRef, property) {
                it.toLong()
            }
        }

    private fun <T> getValue(
        thisRef: Any,
        property: KProperty<*>,
        conversion: (String) -> T,
    ): T? {
        val className = thisRef::class.qualifiedName ?: error("Expecting a full class name")
        val finalName = className.replace(ACCProperties::class.java.name, "ontrack.acceptance")
        val propName = property.name
        val sysProperty = "$finalName.$propName".lowercase()
        val sysValue = System.getProperty(sysProperty)
        return if (!sysValue.isNullOrBlank()) {
            conversion(sysValue)
        } else {
            val envProperty = sysProperty.replace(".", "_").uppercase()
            val envValue = System.getenv(envProperty)
            if (!envValue.isNullOrBlank()) {
                conversion(envValue)
            } else {
                property.findAnnotation<DefaultValue>()
                    ?.value
                    ?.takeIf { it != DefaultValue.NONE }
                    ?.let { conversion(it) }
            }
        }
    }

}