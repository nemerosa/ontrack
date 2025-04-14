package net.nemerosa.ontrack.kdsl.connector.graphql

import com.apollographql.apollo.api.Adapter
import com.apollographql.apollo.api.AnyAdapter
import com.apollographql.apollo.api.CustomScalarAdapters
import com.apollographql.apollo.api.json.JsonReader
import com.apollographql.apollo.api.json.JsonWriter
import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.json.asJson

object jsonCustomTypeAdapter : Adapter<JsonNode> {

    override fun fromJson(reader: JsonReader, customScalarAdapters: CustomScalarAdapters): JsonNode {
        val value = AnyAdapter.fromJson(reader, customScalarAdapters)
        return value.asJson()
    }

    override fun toJson(writer: JsonWriter, customScalarAdapters: CustomScalarAdapters, value: JsonNode) {
        AnyAdapter.toJson(writer, customScalarAdapters, value)
    }

}