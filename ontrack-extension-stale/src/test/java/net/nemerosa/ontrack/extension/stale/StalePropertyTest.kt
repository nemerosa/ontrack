package net.nemerosa.ontrack.extension.stale

import net.nemerosa.ontrack.extension.stale.StaleProperty
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.test.TestUtils
import org.junit.Test

class StalePropertyTest {
    @Test
    fun backward_compatibility_of_json() {
        val json = JsonUtils.`object`()
                .with("disablingDuration", 30)
                .with("deletingDuration", 0)
                .end()
        TestUtils.assertJsonRead(
                StaleProperty(30, 0, null),
                json,
                StaleProperty::class.java
        )
    }
}