package net.nemerosa.ontrack.boot.ui

import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import kotlin.reflect.full.memberProperties

fun URI.map(vararg inputs: Any?): URI {
    val builder = UriComponentsBuilder.fromUri(this)
    // Adds all arguments
    inputs.forEach {
        builder.map(it)
    }
    // OK
    return builder.build().toUri()
}

fun UriComponentsBuilder.map(input: Any?) {
    if (input != null) {
        // Gets all properties of the input object
        input::class.memberProperties.forEach { property ->
            val name = property.name
            val value = property.getter.call(input)
            if (value != null) {
                this.queryParam(
                        name,
                        value.toString()
                )
            }
        }
    }
}
