package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.junit.Test
import kotlin.test.assertEquals

class DefaultURIBuilderTest {

    @Test
    fun `Page link for a promotion level`() {
        assertEquals(
                "promotionLevel",
                DefaultURIBuilder().getEntityPageName(ProjectEntityType.PROMOTION_LEVEL)
        )
    }

    @Test
    fun `Page link for a validation stamp`() {
        assertEquals(
                "validationStamp",
                DefaultURIBuilder().getEntityPageName(ProjectEntityType.VALIDATION_STAMP)
        )
    }

    @Test
    fun `Page link for a validation run`() {
        assertEquals(
                "validationRun",
                DefaultURIBuilder().getEntityPageName(ProjectEntityType.VALIDATION_RUN)
        )
    }

}