package net.nemerosa.ontrack.kdsl.acceptance.tests.jenkins

import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCDSLTestSupport
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.spec.configurations.configurations
import net.nemerosa.ontrack.kdsl.spec.extension.jenkins.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ACCJenkinsProperties : AbstractACCDSLTestSupport() {

    @Test
    fun `Jenkins job property`() {
        val name = uid("j-")
        val url = uid("https://jenkins-")
        ontrack.configurations.jenkins.create(
            JenkinsConfiguration(
                name = name,
                url = url
            )
        )
        project {
            jenkinsJob = JenkinsJobProperty(name, "MyProject")
            assertNotNull(jenkinsJob, "Project Jenkins job property set") {
                assertEquals(name, it.configuration)
                assertEquals("MyProject", it.job)
                assertEquals("$url/job/MyProject", it.url)
            }
            branch {
                jenkinsJob = JenkinsJobProperty(name, "MyBranch")
                assertNotNull(jenkinsJob, "Branch Jenkins job property set") {
                    assertEquals(name, it.configuration)
                    assertEquals("MyBranch", it.job)
                    assertEquals("$url/job/MyBranch", it.url)
                }
                promotion().apply {
                    jenkinsJob = JenkinsJobProperty(name, "MyPromotion")
                    assertNotNull(jenkinsJob, "Promotion level Jenkins job property set") {
                        assertEquals(name, it.configuration)
                        assertEquals("MyPromotion", it.job)
                        assertEquals("$url/job/MyPromotion", it.url)
                    }
                }
                validationStamp().apply {
                    jenkinsJob = JenkinsJobProperty(name, "MyValidation")
                    assertNotNull(jenkinsJob, "Validation stamp Jenkins job property set") {
                        assertEquals(name, it.configuration)
                        assertEquals("MyValidation", it.job)
                        assertEquals("$url/job/MyValidation", it.url)
                    }
                }
            }
        }
    }

    @Test
    fun `Jenkins job property URL with folders`() {
        val name = uid("j-")
        ontrack.configurations.jenkins.create(
            JenkinsConfiguration(
                name = name,
                url = "http://jenkins"
            )
        )
        project {
            jenkinsJob = JenkinsJobProperty(name, "prj/prj-build")
            assertNotNull(jenkinsJob, "Jenkins job property set") {
                assertEquals(
                    "http://jenkins/job/prj/job/prj-build",
                    it.url
                )
            }
        }
        project {
            jenkinsJob = JenkinsJobProperty(name, "prj/job/prj-build")
            assertNotNull(jenkinsJob, "Jenkins job property set") {
                assertEquals(
                    "http://jenkins/job/prj/job/prj-build",
                    it.url
                )
            }
        }
    }

    @Test
    fun `Jenkins build property URL with folders`() {
        val name = uid("j-")
        ontrack.configurations.jenkins.create(
            JenkinsConfiguration(
                name = name,
                url = "http://jenkins"
            )
        )
        project {
            branch {
                build("1") {
                    jenkinsBuild = JenkinsBuildProperty(name, "prj/prj-test/prj-test-build", 1)
                    assertNotNull(jenkinsBuild, "Jenkins build property set") {
                        assertEquals(
                            "http://jenkins/job/prj/job/prj-test/job/prj-test-build/1",
                            it.url
                        )
                    }
                }
            }
        }
    }

}