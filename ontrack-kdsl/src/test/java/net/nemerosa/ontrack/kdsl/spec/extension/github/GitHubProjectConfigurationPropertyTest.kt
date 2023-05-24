package net.nemerosa.ontrack.kdsl.spec.extension.github

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GitHubProjectConfigurationPropertyTest {

    @Test
    fun `Parsing the incoming JSON`() {
        val json = mapOf(
                "configuration" to mapOf(
                        "name" to "cnf_55091461",
                        "url" to "https://github.com",
                        "user" to null,
                        "password" to null,
                        "oauth2Token" to null,
                        "appId" to null,
                        "appPrivateKey" to null,
                        "appInstallationAccountName" to null,
                        "autoMergeToken" to null
                ),
                "repository" to "sample/test",
                "indexationInterval" to 0,
                "issueServiceConfigurationIdentifier" to null
        ).asJson()
        val property = json.parse<GitHubProjectConfigurationProperty>()
        assertEquals("cnf_55091461", property.configuration)
        assertEquals("sample/test", property.repository)
        assertEquals(0, property.indexationInterval)
        assertEquals(null, property.issueServiceConfigurationIdentifier)
    }

}