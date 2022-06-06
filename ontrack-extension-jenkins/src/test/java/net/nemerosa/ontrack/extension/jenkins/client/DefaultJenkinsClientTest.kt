package net.nemerosa.ontrack.extension.jenkins.client

import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals

class DefaultJenkinsClientTest {

    private lateinit var jenkinsClient: JenkinsClient

    @BeforeEach
    fun before() {
        val url = "http://jenkins"
        val client = mockk<RestTemplate>()
        jenkinsClient = DefaultJenkinsClient(url, client)
    }

    @Test
    fun getSimpleJobUrl(): Unit {
        val (name, url) = jenkinsClient.getJob("test")
        assertEquals("test", name)
        assertEquals("http://jenkins/job/test", url)
    }

    @Test
    fun getFolderJobUrl() {
        val (name, url) = jenkinsClient.getJob("test/test-master/test-master-build")
        assertEquals("test-master-build", name)
        assertEquals("http://jenkins/job/test/job/test-master/job/test-master-build", url)
    }

    @Test
    fun getCompleteFolderJobUrl(): Unit {
        val (name, url) = jenkinsClient.getJob("test/job/test-master/job/test-master-build")
        assertEquals("test-master-build", name)
        assertEquals("http://jenkins/job/test/job/test-master/job/test-master-build", url)
    }
}