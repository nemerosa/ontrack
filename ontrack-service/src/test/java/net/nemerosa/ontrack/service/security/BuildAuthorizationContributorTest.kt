package net.nemerosa.ontrack.service.security

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.model.security.PromotionRunCreate
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.ValidationRunCreate
import net.nemerosa.ontrack.model.structure.BranchFixtures
import net.nemerosa.ontrack.model.structure.BuildFixtures
import net.nemerosa.ontrack.model.structure.ProjectEntity
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BuildAuthorizationContributorTest {

    private lateinit var securityService: SecurityService
    private lateinit var buildAuthorizationContributor: BuildAuthorizationContributor

    @BeforeEach
    fun init() {
        securityService = mockk()
        buildAuthorizationContributor = BuildAuthorizationContributor(securityService)
    }

    @Test
    fun `Right to promote`() {
        every {
            securityService.isProjectFunctionGranted(
                any<ProjectEntity>(),
                PromotionRunCreate::class.java
            )
        } returns true
        every {
            securityService.isProjectFunctionGranted(
                any<ProjectEntity>(),
                ValidationRunCreate::class.java
            )
        } returns true
        val branch = BranchFixtures.testBranch()
        val build = BuildFixtures.testBuild(branch)
        assertFalse(buildAuthorizationContributor.appliesTo(branch), "Does not apply to a branch")
        assertTrue(buildAuthorizationContributor.appliesTo(build), "Applies to a build")

        val authorizations = buildAuthorizationContributor.getAuthorizations(mockk(), build)

        assertNotNull(authorizations.firstOrNull { it.name == "build" && it.action == "promote" }) {
            assertTrue(it.authorized, "Promote is authorized")
        }
    }

}