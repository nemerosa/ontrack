package net.nemerosa.ontrack.kdsl.acceptance.tests

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.full.findAnnotation

object ACCProperties {

    object Connection {
        object Mgt {
            @DefaultValue("http://localhost:8800/manage")
            val url: String by fromEnv()
        }
    }

    annotation class DefaultValue(
        val value: String = NONE,
    ) {
        companion object {
            const val NONE = "<NONE>"
        }
    }

    private fun fromEnv(): ReadOnlyProperty<Any, String> =
        ReadOnlyProperty { thisRef, property ->
            val className = thisRef::class.qualifiedName ?: error("Expecting a full class name")
            val propName = property.name
            val sysProperty = "$className.$propName".lowercase()
            val sysValue = System.getProperty(sysProperty)
            if (!sysValue.isNullOrBlank()) {
                sysValue
            } else {
                val envProperty = sysProperty.replace(".", "_").uppercase()
                val envValue = System.getenv(envProperty)
                if (!envValue.isNullOrBlank()) {
                    envValue
                } else {
                    property.findAnnotation<DefaultValue>()
                        ?.value
                        ?.takeIf { it != DefaultValue.NONE }
                        ?: error("No system property $sysProperty not environment variable $envProperty is available.")
                }
            }
        }

}