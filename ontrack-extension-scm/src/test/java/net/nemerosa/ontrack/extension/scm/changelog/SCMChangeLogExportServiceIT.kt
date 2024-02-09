package net.nemerosa.ontrack.extension.scm.changelog

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class SCMChangeLogExportServiceIT : AbstractSCMChangeLogTestSupport() {

    @Autowired
    private lateinit var scmChangeLogService: SCMChangeLogService

    @Autowired
    private lateinit var scmChangeLogExportService: SCMChangeLogExportService

    @Test
    fun `Exporting a change log as plain text without grouping`() {
        prepareChangeLogTestCase { from, to ->

            // Getting the change log
            val changeLog = runBlocking {
                scmChangeLogService.getChangeLog(
                    from = from,
                    to = to,
                )
            }

            // Exporting the change log
            val text = scmChangeLogExportService.export(
                changeLog = changeLog,
                input = null,
            )

            // Checking
            assertEquals(
                """
                    * ISS-21 Some new feature
                    * ISS-22 Some fixes are needed
                    * ISS-23 Some nicer UI
                    """.trimIndent(),
                text
            )
        }
    }

    @Test
    fun `Exporting a change log as markdown with grouping`() {
        prepareChangeLogTestCase { from, to ->

            // Getting the change log
            val changeLog = runBlocking {
                scmChangeLogService.getChangeLog(
                    from = from,
                    to = to,
                )
            }

            // Exporting the change log
            val text = scmChangeLogExportService.export(
                changeLog = changeLog,
                input = SCMChangeLogExportInput(
                    format = "markdown",
                    grouping = "Defects=defect|Features=feature,enhancement"
                ),
            )

            // Checking
            assertEquals(
                """
                    ## Defects
                    
                    * ISS-22 Some fixes are needed
                    
                    ## Features
                    
                    * ISS-21 Some new feature
                    * ISS-23 Some nicer UI
                    """.trimIndent(),
                text
            )
        }
    }

}