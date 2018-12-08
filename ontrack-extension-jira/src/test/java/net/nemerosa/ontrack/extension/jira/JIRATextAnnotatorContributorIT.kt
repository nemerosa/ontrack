package net.nemerosa.ontrack.extension.jira

import net.nemerosa.ontrack.model.support.MessageAnnotationUtils
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class JIRATextAnnotatorContributorIT : AbstractJIRATestSupport() {

    @Autowired
    private lateinit var jiraTextAnnotatorContributor: JIRATextAnnotatorContributor

    @Test
    fun `JIRA text annotation on non configured project`() {
        project {
            val annotated = MessageAnnotationUtils.annotate(
                    "Some text with issue ISSUE-1234",
                    listOf(
                            jiraTextAnnotatorContributor.getMessageAnnotator(this)
                    )
            )
            assertEquals(
                    "Some text with issue ISSUE-1234",
                    annotated
            )
        }
    }

    @Test
    fun `JIRA text annotation on configured project`() {
        project {
            // TODO Project configuration (with mock Issue service configuration provider)
            val annotated = MessageAnnotationUtils.annotate(
                    "Some text with issue ISSUE-1234",
                    listOf(
                            jiraTextAnnotatorContributor.getMessageAnnotator(this)
                    )
            )
            assertEquals(
                    "Some text with issue ISSUE-1234",
                    annotated
            )
        }
    }


}