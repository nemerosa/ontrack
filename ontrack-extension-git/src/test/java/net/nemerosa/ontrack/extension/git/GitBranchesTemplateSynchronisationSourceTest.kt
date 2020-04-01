package net.nemerosa.ontrack.extension.git

import net.nemerosa.ontrack.extension.git.model.BasicGitActualConfiguration
import net.nemerosa.ontrack.extension.git.model.BasicGitConfiguration
import net.nemerosa.ontrack.extension.git.model.GitConfiguration
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import net.nemerosa.ontrack.model.structure.Project
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GitBranchesTemplateSynchronisationSourceTest {

    private lateinit var gitService: GitService
    private lateinit var source: GitBranchesTemplateSynchronisationSource
    private lateinit var gitConfiguration: GitConfiguration
    private lateinit var branch: Branch
    private lateinit var project: Project

    @Before
    fun before() {
        project = Project.of(nd("P", "Project"))
        branch = Branch.of(project, nd("B", "Branch"))
        gitConfiguration = BasicGitActualConfiguration.of(BasicGitConfiguration.empty())
        gitService = mock(GitService::class.java)
        `when`(gitService.getProjectConfiguration(project)).thenReturn(gitConfiguration)
        `when`(gitService.getRemoteBranches(gitConfiguration)).thenReturn(
                listOf("master", "feature/ontrack-40-templating", "feature/ontrack-111-project-manager", "fix/ontrack-110")
        )
        source = GitBranchesTemplateSynchronisationSource(
                gitService
        )
    }

    @Test
    fun `Not applicable if branch not configured for Git`() {
        `when`(gitService.isBranchConfiguredForGit(branch)).thenReturn(false)
        assertFalse(source.isApplicable(branch))
    }

    @Test
    fun `Applicable if branch configured for Git`() {
        `when`(gitService.isBranchConfiguredForGit(branch)).thenReturn(true)
        assertTrue(source.isApplicable(branch))
    }

    @Test
    fun `Branches - no filter`() {
        assertEquals(
                listOf("feature/ontrack-111-project-manager", "feature/ontrack-40-templating", "fix/ontrack-110", "master"),
                source.getBranchNames(branch, GitBranchesTemplateSynchronisationSourceConfig("", ""))
        )
    }

    @Test
    fun `Branches - includes all`() {
        assertEquals(
                listOf("feature/ontrack-111-project-manager", "feature/ontrack-40-templating", "fix/ontrack-110", "master"),
                source.getBranchNames(branch, GitBranchesTemplateSynchronisationSourceConfig("*", ""))
        )
    }

    @Test
    fun `Branches - exclude master`() {
        assertEquals(
                listOf("feature/ontrack-111-project-manager", "feature/ontrack-40-templating", "fix/ontrack-110"),
                source.getBranchNames(branch, GitBranchesTemplateSynchronisationSourceConfig("", "master"))
        )
    }

    @Test
    fun `Branches - include only`() {
        assertEquals(
                listOf("fix/ontrack-110"),
                source.getBranchNames(branch, GitBranchesTemplateSynchronisationSourceConfig("fix/*", ""))
        )
    }

    @Test
    fun `Branches - include and exclude`() {
        assertEquals(
                listOf("feature/ontrack-111-project-manager"),
                source.getBranchNames(branch, GitBranchesTemplateSynchronisationSourceConfig("feature/*", "*templating"))
        )
    }

}