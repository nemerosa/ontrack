package net.nemerosa.ontrack.extension.scm.relnotes

import net.nemerosa.ontrack.extension.issues.export.TextIssueExportService
import net.nemerosa.ontrack.model.structure.*
import org.junit.Test
import kotlin.test.assertEquals

class ReleaseNotesTest {

    private val exportService = TextIssueExportService()

    val build: Build = Build.of(
            Branch.of(
                    Project.of(NameDescription.nd("P", "")),
                    NameDescription.nd("master", "")
            ),
            NameDescription.nd("1.0.0", ""),
            Signature.of("test")
    )

    @Test
    fun concatenation() {
        assertEquals(
                "* #1 Feature\n* #2 Defect\n",
                exportService.concatText(
                        listOf(
                                "* #1 Feature\n",
                                "* #2 Defect\n"
                        )
                )
        )
    }

    @Test
    fun exportVersion() {
        assertEquals(
                """
                ## 1.0.0
                
                * #1 Feature
                * #2 Defect
                """.trimIndent(),
                exportService.exportReleaseNotesVersion(
                        ReleaseNotesVersion(
                                build,
                                """
                                    * #1 Feature
                                    * #2 Defect
                                """.trimIndent()
                        )
                ).content.toString(Charsets.UTF_8)
        )
    }

}