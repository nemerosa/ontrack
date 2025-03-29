package net.nemerosa.ontrack.ui.resource

import com.fasterxml.jackson.databind.JsonNode
import io.mockk.mockk
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.ui.controller.MockURIBuilder
import org.junit.jupiter.api.BeforeEach
import kotlin.test.assertEquals

abstract class AbstractResourceTest {
    protected lateinit var mapper: ResourceObjectMapper
    protected lateinit var securityService: SecurityService

    @BeforeEach
    fun before() {
        securityService = mockk<SecurityService>()
        mapper = ResourceObjectMapperFactory().resourceObjectMapper(
            emptyList(),
            DefaultResourceContext(MockURIBuilder(), securityService)
        )
    }

    companion object {
        fun assertResourceJson(mapper: ResourceObjectMapper, expectedJson: JsonNode?, o: Any?) {
            assertEquals(
                mapper.objectMapper.writeValueAsString(expectedJson),
                mapper.write(o)
            )
        }

        fun assertResourceJson(mapper: ResourceObjectMapper, expectedJson: JsonNode?, o: Any?, view: Class<*>?) {
            assertEquals(
                mapper.objectMapper.writeValueAsString(expectedJson),
                mapper.write(o, view)
            )
        }
    }
}
