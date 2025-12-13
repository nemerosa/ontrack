package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.core.JsonProcessingException
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.test.TestUtils
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BuildRequestTest {

    @Test
    fun a_null_properties_list() {
        assertEquals(
            emptyList<PropertyCreationRequest>(),
            BuildRequest(
                "12",
                "Build 12",
                null
            ).properties
        )
    }

    @Test
    fun name_description() {
        assertEquals(
            NameDescription("12", "Build 12"),
            BuildRequest(
                "12",
                "Build 12",
                emptyList<PropertyCreationRequest>()
            ).asNameDescription()
        )
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun from_json_without_properties() {
        TestUtils.assertJsonRead<BuildRequest?>(
            BuildRequest(
                "12",
                "Build 12",
                emptyList<PropertyCreationRequest>()
            ),
            mapOf(
                "name" to "12",
                "description" to "Build 12",
            ).asJson(),
            BuildRequest::class.java
        )
    }

    @Test
    @Throws(JsonProcessingException::class)
    fun from_json_with_properties() {
        TestUtils.assertJsonRead<BuildRequest?>(
            BuildRequest(
                "12",
                "Build 12",
                listOf(
                    PropertyCreationRequest(
                        "build",
                        mapOf("url" to "http://ci/build/12").asJson()
                    )
                )
            ),
            mapOf(
                "name" to "12",
                "description" to "Build 12",
                "properties" to listOf(
                    mapOf(
                        "propertyTypeName" to "build",
                        "propertyData" to mapOf("url" to "http://ci/build/12"),
                    )
                )
            ).asJson(),
            BuildRequest::class.java
        )
    }
}
