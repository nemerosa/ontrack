package net.nemerosa.ontrack.extension.notifications.core

import net.nemerosa.ontrack.extension.notifications.AbstractNotificationTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.model.exceptions.BranchNotFoundException
import net.nemerosa.ontrack.model.exceptions.BuildNotFoundException
import net.nemerosa.ontrack.model.exceptions.ProjectNotFoundException
import net.nemerosa.ontrack.model.structure.BuildDisplayNameService
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@AsAdminTest
class YontrackBuildNotificationHelperIT : AbstractNotificationTestSupport() {

    @Autowired
    private lateinit var helper: YontrackBuildNotificationHelper

    @Autowired
    private lateinit var buildDisplayNameService: BuildDisplayNameService

    @Test
    fun `Get branch from event context`() {
        val branch = doCreateBranch()
        val event = eventFactory.newBranch(branch)
        val result = helper.getBranch(event, null, null)
        assertEquals(branch.id, result.id)
    }

    @Test
    fun `Get branch from name`() {
        val branch = doCreateBranch()
        val event = eventFactory.newProject(branch.project)
        val result = helper.getBranch(event, null, branch.name)
        assertEquals(branch.id, result.id)
    }

    @Test
    fun `Get branch from name and project name`() {
        val branch = doCreateBranch()
        val otherProject = doCreateProject()
        val event = eventFactory.newProject(otherProject)
        val result = helper.getBranch(event, branch.project.name, branch.name)
        assertEquals(branch.id, result.id)
    }

    @Test
    fun `Get branch not found`() {
        val project = doCreateProject()
        val event = eventFactory.newProject(project)
        assertFailsWith<BranchNotFoundException> {
            helper.getBranch(event, null, "unknown")
        }
    }

    @Test
    fun `Get project not found`() {
        val project = doCreateProject()
        val event = eventFactory.newProject(project)
        assertFailsWith<ProjectNotFoundException> {
            helper.getBranch(event, "unknown", "branch")
        }
    }

    @Test
    fun `Get build from event context`() {
        val build = doCreateBuild()
        val event = eventFactory.newBuild(build)
        val result = helper.getBuild(event, null, null, null)
        assertEquals(build.id, result.id)
    }

    @Test
    fun `Get build from name`() {
        val build = doCreateBuild()
        val event = eventFactory.newBranch(build.branch)
        val result = helper.getBuild(event, null, null, build.name)
        assertEquals(build.id, result.id)
    }

    @Test
    fun `Get build from name and branch name`() {
        val build = doCreateBuild()
        val otherBranch = doCreateBranch(project = build.project)
        val event = eventFactory.newBranch(otherBranch)
        val result = helper.getBuild(event, null, build.branch.name, build.name)
        assertEquals(build.id, result.id)
    }

    @Test
    fun `Get build from name and project name and branch name`() {
        val build = doCreateBuild()
        val otherProject = doCreateProject()
        val event = eventFactory.newProject(otherProject)
        val result = helper.getBuild(event, build.project.name, build.branch.name, build.name)
        assertEquals(build.id, result.id)
    }

    @Test
    fun `Get build from name and project name if branch name not specified`() {
        val build = doCreateBuild()
        val otherProject = doCreateProject()
        val event = eventFactory.newProject(otherProject)
        val result = helper.getBuild(event, build.project.name, null, build.name)
        assertEquals(build.id, result.id)
    }

    @Test
    fun `Get build by display name`() {
        val build = doCreateBuild()
        val displayName = uid("BDN")
        buildDisplayNameService.setDisplayName(build, displayName, true)

        val event = eventFactory.newBranch(build.branch)
        val result = helper.getBuild(event, null, null, displayName)
        assertEquals(build.id, result.id)
    }

    @Test
    fun `Get build not found`() {
        val branch = doCreateBranch()
        val event = eventFactory.newBranch(branch)
        assertFailsWith<BuildNotFoundException> {
            helper.getBuild(event, null, null, "unknown")
        }
    }
}
