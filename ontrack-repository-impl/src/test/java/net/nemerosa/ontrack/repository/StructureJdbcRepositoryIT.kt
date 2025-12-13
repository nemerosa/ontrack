package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ID.Companion.isDefined
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.Signature
import net.nemerosa.ontrack.test.TestUtils
import org.apache.commons.lang3.StringUtils
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StructureJdbcRepositoryIT : AbstractRepositoryTestSupport() {

    @Test
    fun create_branch_project_not_defined() {
        assertFailsWith<IllegalStateException> {
            structureRepository.newBranch(
                Branch.of(
                    Project.of(nameDescription()),
                    nameDescription()
                )
            )
        }
    }

    @Test
    fun create_branch() {
        // Creates a project
        val project: Project = do_create_project()
        // Creates a branch for this project
        val branch: Branch? = structureRepository.newBranch(Branch.of(project, nameDescription()))
        // Checks
        assertNotNull(branch, "Branch is defined")
        assertTrue(isDefined(branch.id), "Branch ID is defined")
    }

    @Test
    fun create_build_with_long_name() {
        // Creates a ranch
        val branch: Branch = do_create_branch()
        // Creates a build for this branch, with a very long name
        val buildName = StringUtils.repeat("b", 150)
        val build: Build = structureRepository.newBuild(
            Build.of(
                branch,
                nd(buildName, ""),
                Signature.of("test")
            )
        )
        // Checks
        assertNotNull(build, "Build is defined")
        assertTrue(isDefined(build.id), "Build ID is defined")
    }

    @Test
    fun create_branch_with_null_description() {
        // Creates a project
        val project: Project = do_create_project()
        // Creates a branch for this project
        val branch: Branch = structureRepository.newBranch(Branch.of(project, nd("B", null)))
        // Checks
        assertNotNull(branch, "Branch is defined")
        assertNull(branch.description)
        assertTrue(isDefined(branch.id), "Branch ID is defined")
    }

    @Test
    fun save_branch_disabled() {
        // Creates a project
        val project: Project = do_create_project()
        // Creates a branch for this project
        var branch: Branch = structureRepository.newBranch(Branch.of(project, nameDescription()))
        // Disables it
        branch = branch.withDisabled(true)
        // Saves it
        structureRepository.saveBranch(branch)
        // Retrieves it
        branch = structureRepository.getBranch(branch.id)
        // Checks it is disabled
        assertTrue(branch.isDisabled, "Branch must be disabled")
    }

    @Test
    fun create_project() {
        val p: Project = do_create_project()
        assertNotNull(p, "Project is defined")
        assertTrue(isDefined(p.id), "Project ID is defined")
    }

    @Test
    fun create_project_with_null_description() {
        val p: Project = structureRepository.newProject(Project.of(nd(TestUtils.uid("P"), null)))
        assertNotNull(p, "Project is defined")
        assertNull(p.description)
        assertTrue(isDefined(p.id), "Project ID is defined")
    }

    @Test
    fun save_project_disabled() {
        var p: Project = do_create_project()
        p = p.withDisabled(true)
        structureRepository.saveProject(p)
        p = structureRepository.getProject(p.id)
        assertTrue(p.isDisabled, "Project must be disabled")
    }
}
