package net.nemerosa.ontrack.kdsl.connector.graphql

import com.apollographql.apollo.api.CustomTypeAdapter
import com.apollographql.apollo.api.CustomTypeValue
import com.apollographql.apollo.api.ScalarType
import java.util.*

class UUIDCustomTypeAdapter : CustomTypeAdapter<UUID> {

    override fun decode(value: CustomTypeValue<*>): UUID {
        val s = value.value?.toString()?.takeIf { it.isNotBlank() }
            ?: error("Cannot parse blank or null into a UUID")
        return UUID.fromString(s)
    }

    override fun encode(value: UUID): CustomTypeValue<*> {
        return CustomTypeValue.GraphQLString(value.toString())
    }

    companion object {
        val TYPE = object : ScalarType {
            override fun typeName(): String = "UUID"
            override fun className(): String = UUID::class.java.name
        }
    }

}