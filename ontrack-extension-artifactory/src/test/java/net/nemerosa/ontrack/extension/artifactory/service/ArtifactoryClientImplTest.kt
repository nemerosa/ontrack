package net.nemerosa.ontrack.extension.artifactory.service

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.client.ClientNotFoundException
import net.nemerosa.ontrack.client.JsonClient
import net.nemerosa.ontrack.extension.artifactory.client.ArtifactoryClientImpl
import net.nemerosa.ontrack.json.JsonUtils
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ArtifactoryClientImplTest {

    @Test
    fun buildNumbers() {
        val jsonClient = mockk<JsonClient>()
        every {
            jsonClient["/api/build/%s", "PROJECT"]
        } returns JsonUtils.`object`()
            .with(
                "buildsNumbers", JsonUtils.array()
                    .with(JsonUtils.`object`().with("uri", "/1").end())
                    .with(JsonUtils.`object`().with("uri", "/2").end())
                    .end()
            )
            .end()
        val client = ArtifactoryClientImpl(jsonClient)
        assertEquals(
            mutableListOf("1", "2"),
            client.getBuildNumbers("PROJECT")
        )
    }

    @Test
    fun buildNumbersEmptyForBuildNotFound() {
        val jsonClient = mockk<JsonClient>()
        every {
            jsonClient["/api/build/%s", "PROJECT"]
        } throws ClientNotFoundException("Not found")
        val client = ArtifactoryClientImpl(jsonClient)
        assertEquals(
            emptyList<String>(),
            client.getBuildNumbers("PROJECT")
        )
    }
}
