package net.nemerosa.ontrack.extension.support.client

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.format
import net.nemerosa.ontrack.json.parseAsJson
import org.hamcrest.CustomTypeSafeMatcher

class JsonBodyMatcher(
    private val expectedBody: Any,
) : CustomTypeSafeMatcher<String>(
    expectedBody.asJson().format()
) {

    override fun matchesSafely(item: String?): Boolean {
        val actualJson = item?.parseAsJson()
        val expectedJson = expectedBody.asJson()
        return actualJson == expectedJson
    }

}