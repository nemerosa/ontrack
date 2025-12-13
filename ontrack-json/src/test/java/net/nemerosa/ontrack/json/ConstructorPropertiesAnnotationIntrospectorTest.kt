package net.nemerosa.ontrack.json

import net.nemerosa.ontrack.json.ObjectMapperFactory.create
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Test

class ConstructorPropertiesAnnotationIntrospectorTest {

    private data class ImmutablePojo(val name: String, val value: Int)

    private val instance = ImmutablePojo("foobar", 42)

    @Test
    fun testJacksonAbleToDeserialize() {
        val mapper = create()
        val json = mapper.writeValueAsString(instance)
        val output = mapper.readValue(json, ImmutablePojo::class.java)
        MatcherAssert.assertThat(output, CoreMatchers.equalTo(instance))
    }
}
