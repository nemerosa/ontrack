package net.nemerosa.ontrack.model

import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.test.TestUtils.assertJsonRead
import net.nemerosa.ontrack.test.TestUtils.assertJsonWrite
import org.junit.Test

class KotlinPOJOTest {

    @Test
    fun json_to_kotlin() {
        assertJsonRead<KotlinPOJO>(
                KotlinPOJO("Name", 10),
                JsonUtils.`object`()
                        .with("name", "Name")
                        .with("value", 10)
                        .with("doubleValue", 20)
                        .end(),
                KotlinPOJO::class.java
        )
    }

    @Test
    fun kotlin_to_json() {
        assertJsonWrite(
                JsonUtils.`object`()
                        .with("name", "Name")
                        .with("value", 10)
                        .with("doubleValue", 20)
                        .end(),
                KotlinPOJO("Name", 10)
        )
    }

}