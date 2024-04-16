package net.nemerosa.ontrack.extension.support.client

import net.nemerosa.ontrack.json.asJson
import org.hamcrest.CustomMatcher

class JsonBodyMatcher(
    private val expectedBody: Any,
) : CustomMatcher<String>("JSON body matcher") {

    override fun matches(content: Any?): Boolean {
        val actualJson = content.asJson()
        val expectedJson = expectedBody.asJson()
        return actualJson == expectedJson
    }

}