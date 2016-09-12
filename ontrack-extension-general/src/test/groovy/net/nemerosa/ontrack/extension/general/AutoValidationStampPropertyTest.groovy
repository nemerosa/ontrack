package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.json.JsonUtils
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.assertJsonRead

class AutoValidationStampPropertyTest {

    @Test
    void 'Backward compatible JSON'() {
        assertJsonRead(
                new AutoValidationStampProperty(true, false),
                JsonUtils.object().with("autoCreate", true).end(),
                AutoValidationStampProperty
        )
    }

    @Test
    void 'New JSON format'() {
        assertJsonRead(
                new AutoValidationStampProperty(true, true),
                JsonUtils.object().with("autoCreate", true).with("autoCreateIfNotPredefined", true).end(),
                AutoValidationStampProperty
        )
    }

}
