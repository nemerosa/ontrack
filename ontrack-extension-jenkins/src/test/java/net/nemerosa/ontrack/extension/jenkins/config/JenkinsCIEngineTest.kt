package net.nemerosa.ontrack.extension.jenkins.config

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JenkinsCIEngineTest {

    @Test
    fun `Jenkins detection`() {
        val engine = JenkinsCIEngine()
        assertTrue(engine.matchesEnv(mapOf("JENKINS_URL" to "uri:jenkins")))
        assertFalse(engine.matchesEnv(mapOf("JENKINS_URL" to "")))
        assertFalse(engine.matchesEnv(emptyMap()))
    }

    @Test
    fun `Jenkins SCM URL`() {
        val engine = JenkinsCIEngine()
        assertNull(engine.getScmUrl(emptyMap()))
        assertEquals(
            "https://github.com/nemerosa/ontrack.git",
            engine.getScmUrl(mapOf("GIT_URL" to "https://github.com/nemerosa/ontrack.git"))
        )
    }

    @Test
    fun `Jenkins project name`() {
        val engine = JenkinsCIEngine()
        assertEquals(
            "yontrack", engine.getProjectName(
                mapOf(
                    "PROJECT_NAME" to "yontrack",
                    "GIT_URL" to "https://github.com/nemerosa/ontrack.git",
                )
            )
        )
        assertEquals(
            "ontrack", engine.getProjectName(
                mapOf(
                    "GIT_URL" to "https://github.com/nemerosa/ontrack.git",
                )
            )
        )
    }

    @Test
    fun `Jenkins branch name`() {
        val engine = JenkinsCIEngine()
        assertEquals("main", engine.getBranchName(mapOf("BRANCH_NAME" to "main")))
        assertEquals(null, engine.getBranchName(emptyMap()))
    }

    @Test
    fun `Jenkins build suffix is the build number`() {
        val engine = JenkinsCIEngine()
        assertEquals("23", engine.getBuildSuffix(mapOf("BUILD_NUMBER" to "23")))
        assertEquals(null, engine.getBuildSuffix(mapOf("NO_BUILD_NUMBER" to "23")))
    }

}