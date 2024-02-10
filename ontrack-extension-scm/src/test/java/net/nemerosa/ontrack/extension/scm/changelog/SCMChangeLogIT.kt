package net.nemerosa.ontrack.extension.scm.changelog

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.fail

class SCMChangeLogIT : AbstractSCMChangeLogTestSupport() {

    @Autowired
    private lateinit var scmChangeLogService: SCMChangeLogService

    @Test
    fun `Getting a change log using the SCM API`() {
        prepareChangeLogTestCase { _, from, to ->
            val changeLog = runBlocking {
                scmChangeLogService.getChangeLog(
                    from = from,
                    to = to,
                )
            } ?: fail("Could not get a change log")

            // Checking the commits
            assertEquals(
                listOf(
                    "ISS-23 Fixing some CSS",
                    "ISS-22 Fixing some bugs",
                    "ISS-21 Some fixes for a feature",
                    "ISS-21 Some commits for a feature",
                ),
                changeLog.commits.map { it.commit.message },
                "Change log commits"
            )

            // Checking the issues
            assertEquals(
                listOf(
                    "ISS-21" to "Some new feature",
                    "ISS-22" to "Some fixes are needed",
                    "ISS-23" to "Some nicer UI",
                ),
                changeLog.issues?.issues?.map { it.displayKey to it.summary },
                "Change log issues"
            )

        }
    }
}