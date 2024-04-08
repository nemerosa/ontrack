package net.nemerosa.ontrack.extension.notifications.subscriptions

import io.mockk.mockk
import net.nemerosa.ontrack.model.structure.*
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ProjectEntitySubscriptionsAuthorizationContributorTest {

    @Test
    fun `Applies only to stable entities`() {
        val contributor = ProjectEntitySubscriptionsAuthorizationContributor(mockk())

        val project = ProjectFixtures.testProject()
        val branch = BranchFixtures.testBranch(project = project)
        val validationStamp = ValidationStampFixtures.testValidationStamp(branch = branch)
        val build = BuildFixtures.testBuild(branch = branch)
        val validationRun = ValidationRunFixtures.testValidationRun(validationStamp = validationStamp, build = build)
        val promotionLevel = PromotionLevelFixtures.testPromotionLevel(branch = branch)
        val promotionRun = PromotionRunFixtures.testPromotionRun(promotionLevel = promotionLevel, build = build)

        assertTrue(contributor.appliesTo(project))
        assertTrue(contributor.appliesTo(branch))
        assertTrue(contributor.appliesTo(validationStamp))
        assertTrue(contributor.appliesTo(promotionLevel))

        assertFalse(contributor.appliesTo(build))
        assertFalse(contributor.appliesTo(validationRun))
        assertFalse(contributor.appliesTo(promotionRun))
    }

}