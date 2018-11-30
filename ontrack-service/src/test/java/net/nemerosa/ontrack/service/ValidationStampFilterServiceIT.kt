package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.exceptions.ValidationStampFilterNameAlreadyDefinedException
import net.nemerosa.ontrack.model.exceptions.ValidationStampFilterNotFoundException
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.ProjectConfig
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.ValidationStampFilter
import net.nemerosa.ontrack.model.structure.ValidationStampFilterService
import net.nemerosa.ontrack.test.TestUtils.uid
import org.apache.commons.lang3.StringUtils
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

class ValidationStampFilterServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var filterService: ValidationStampFilterService

    private lateinit var branch: Branch

    @Before
    fun setup() {
        branch = doCreateBranch()
    }

    /**
     * Regression test for #628
     */
    @Test
    fun `Deleting a validation stamp used by validation stamp filter`() {
        project {
            branch {
                // Creates a validation stamp
                val vs = validationStamp()
                // Creates a build
                build {
                    // Validates this build
                    validate(vs)
                }
                // Creates a validation stamp filter containing the validation stamp for this branch
                val vsf = validationStampFilter(branch = this, vsNames = listOf(vs.name))
                // Makes sure we can download all filters for this branch
                val filters: List<ValidationStampFilter> = filterService.getBranchValidationStampFilters(this, false)
                assertEquals(
                        listOf(vsf.name),
                        filters.map { it.name }
                )
                assertEquals(
                        listOf(listOf(vs.name)),
                        filters.map { it.vsNames }
                )
                // Now, deletes the validation stamp
                asAdmin().execute {
                    structureService.deleteValidationStamp(vs.id)
                }
                // Makes sure we can download all filters for this branch
                val newFilters: List<ValidationStampFilter> = filterService.getBranchValidationStampFilters(this, false)
                assertEquals(
                        listOf(vsf.name),
                        newFilters.map { it.name }
                )
                // ... and that the name is NOT gone from the list
                assertEquals(
                        listOf(listOf(vs.name)),
                        filters.map { it.vsNames }
                )
            }
        }
    }

    @Test
    @Throws(Exception::class)
    fun new_global_filter() {
        asUser().with(GlobalSettings::class.java).execute {
            val filter = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name("My filter")
                            .vsNames(listOf("CI"))
                            .build()
            )
            assertTrue(filter.id.isSet)
        }
    }

    @Test(expected = ValidationStampFilterNameAlreadyDefinedException::class)
    @Throws(Exception::class)
    fun new_global_filter_with_existing_name() {
        asUser().with(GlobalSettings::class.java).execute {
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name("My filter")
                            .vsNames(listOf("CI"))
                            .build()
            )
            // Creates another filter with the same name
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name("My filter")
                            .vsNames(listOf("OTHER"))
                            .build()
            )
        }
    }

    @Test(expected = IllegalStateException::class)
    @Throws(Exception::class)
    fun new_filter_with_both_project_and_branch() {
        asUser().with(branch, ProjectConfig::class.java).execute {
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name("My filter")
                            .project(branch.project)
                            .branch(branch)
                            .vsNames(listOf("CI"))
                            .build()
            )
        }
    }

    @Test
    @Throws(Exception::class)
    fun new_project_filter() {
        asUser().with(branch, ProjectConfig::class.java).execute {
            val filter = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name("My filter")
                            .project(branch.project)
                            .vsNames(listOf("CI"))
                            .build()
            )
            assertTrue(filter.id.isSet)
        }
    }

    @Test(expected = ValidationStampFilterNameAlreadyDefinedException::class)
    @Throws(Exception::class)
    fun new_project_filter_with_existing_name() {
        asUser().with(branch, ProjectConfig::class.java).execute {
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name("My filter")
                            .project(branch.project)
                            .vsNames(listOf("CI"))
                            .build()
            )
            // Creates another filter with the same name
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name("My filter")
                            .project(branch.project)
                            .vsNames(listOf("OTHER"))
                            .build()
            )
        }
    }

    @Test
    @Throws(Exception::class)
    fun new_project_filter_with_same_name_than_global() {
        asUser().with(GlobalSettings::class.java).with(branch, ProjectConfig::class.java).execute {
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name("My filter")
                            .vsNames(listOf("CI"))
                            .build()
            )
            val filter = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name("My filter")
                            .project(branch.project)
                            .vsNames(listOf("CI"))
                            .build()
            )
            assertTrue(filter.id.isSet)
        }
    }

    @Test
    @Throws(Exception::class)
    fun new_branch_filter() {
        asUser().with(branch, ProjectConfig::class.java).execute {
            val filter = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name("My filter")
                            .branch(branch)
                            .vsNames(listOf("CI"))
                            .build()
            )
            assertTrue(filter.id.isSet)
        }
    }

    @Test(expected = ValidationStampFilterNameAlreadyDefinedException::class)
    @Throws(Exception::class)
    fun new_branch_filter_with_existing_name() {
        asUser().with(branch, ProjectConfig::class.java).execute {
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name("My filter")
                            .branch(branch)
                            .vsNames(listOf("CI"))
                            .build()
            )
            // Creates another filter with the same name
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name("My filter")
                            .branch(branch)
                            .vsNames(listOf("OTHER"))
                            .build()
            )
        }
    }

    @Test
    @Throws(Exception::class)
    fun new_branch_filter_with_same_name_than_global() {
        asUser().with(GlobalSettings::class.java).with(branch, ProjectConfig::class.java).execute {
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name("My filter")
                            .vsNames(listOf("CI"))
                            .build()
            )
            val filter = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name("My filter")
                            .branch(branch)
                            .vsNames(listOf("CI"))
                            .build()
            )
            assertTrue(filter.id.isSet)
        }
    }

    @Test
    @Throws(Exception::class)
    fun new_branch_filter_with_same_name_than_project() {
        asUser().with(GlobalSettings::class.java).with(branch, ProjectConfig::class.java).execute {
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name("My filter")
                            .project(branch.project)
                            .vsNames(listOf("CI"))
                            .build()
            )
            val filter = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name("My filter")
                            .branch(branch)
                            .vsNames(listOf("CI"))
                            .build()
            )
            assertTrue(filter.id.isSet)
        }
    }

    @Throws(Exception::class)
    private fun createFilters(): String {
        return asUser().with(GlobalSettings::class.java).with(branch, ProjectConfig::class.java).call {
            val name = uid("F")
            // Global
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(name)
                            .vsNames(listOf("GLOBAL"))
                            .build()
            )
            // Project
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(name)
                            .project(branch.project)
                            .vsNames(listOf("PROJECT"))
                            .build()
            )
            // Branch
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(name)
                            .branch(branch)
                            .vsNames(listOf("BRANCH"))
                            .build()
            )
            // OK
            name
        }
    }

    @Test
    @Throws(Exception::class)
    fun global_filters() {
        val name = createFilters()
        asUser().with(GlobalSettings::class.java).execute {
            val list = filterService.globalValidationStampFilters
                    .filter { f -> StringUtils.equals(name, f.name) }
            assertEquals(1, list.size.toLong())
            assertEquals(name, list[0].name)
            assertEquals(listOf("GLOBAL"), list[0].vsNames)
        }
    }

    @Test
    @Throws(Exception::class)
    fun project_filters_only() {
        val name = createFilters()
        val list = asUser().withView(branch).call {
            filterService.getProjectValidationStampFilters(branch.project, false)
                    .filter { f -> StringUtils.equals(name, f.name) }
        }
        assertEquals(1, list.size.toLong())
        assertEquals(name, list[0].name)
        assertEquals(listOf("PROJECT"), list[0].vsNames)
    }

    @Test
    @Throws(Exception::class)
    fun project_filters_include_all() {
        val name = createFilters()
        val list = asUser().withView(branch).call {
            filterService.getProjectValidationStampFilters(branch.project, true)
                    .filter { f -> StringUtils.equals(name, f.name) }
        }
        assertEquals(1, list.size.toLong())
        assertEquals(name, list[0].name)
        assertEquals(listOf("PROJECT"), list[0].vsNames)
    }

    @Test
    @Throws(Exception::class)
    fun branch_filters_only() {
        val name = createFilters()
        val list = asUser().withView(branch).call {
            filterService.getBranchValidationStampFilters(branch, false)
                    .filter { f -> StringUtils.equals(name, f.name) }
        }
        assertEquals(1, list.size.toLong())
        assertEquals(name, list[0].name)
        assertEquals(listOf("BRANCH"), list[0].vsNames)
    }

    @Test
    @Throws(Exception::class)
    fun branch_filters_include_all() {
        val name = createFilters()
        val list = asUser().withView(branch).call {
            filterService.getBranchValidationStampFilters(branch, true)
                    .filter { f -> StringUtils.equals(name, f.name) }
        }
        assertEquals(1, list.size.toLong())
        assertEquals(name, list[0].name)
        assertEquals(listOf("BRANCH"), list[0].vsNames)
    }

    @Test
    @Throws(Exception::class)
    fun by_name_global_only() {
        val name = uid("F")
        asUser().with(GlobalSettings::class.java).execute {
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(name)
                            .vsNames(listOf("GLOBAL"))
                            .build()
            )
        }
        val f = asUser().withView(branch).call { filterService.getValidationStampFilterByName(branch, name).orElse(null) }
        assertNotNull(f)
        assertEquals(name, f.name)
        assertEquals(listOf("GLOBAL"), f.vsNames)
    }

    @Test
    @Throws(Exception::class)
    fun by_name_global_and_project() {
        val name = uid("F")
        asUser().with(GlobalSettings::class.java).with(branch, ProjectConfig::class.java).execute {
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(name)
                            .vsNames(listOf("GLOBAL"))
                            .build()
            )
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(name)
                            .project(branch.project)
                            .vsNames(listOf("PROJECT"))
                            .build()
            )
        }
        val f = asUser().withView(branch).call { filterService.getValidationStampFilterByName(branch, name).orElse(null) }
        assertNotNull(f)
        assertEquals(name, f.name)
        assertEquals(listOf("PROJECT"), f.vsNames)
    }

    @Test
    @Throws(Exception::class)
    fun by_name_branch() {
        val name = uid("F")
        asUser().with(GlobalSettings::class.java).with(branch, ProjectConfig::class.java).execute {
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(name)
                            .vsNames(listOf("GLOBAL"))
                            .build()
            )
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(name)
                            .project(branch.project)
                            .vsNames(listOf("PROJECT"))
                            .build()
            )
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(name)
                            .branch(branch)
                            .vsNames(listOf("BRANCH"))
                            .build()
            )
        }
        val f = asUser().withView(branch).call { filterService.getValidationStampFilterByName(branch, name).orElse(null) }
        assertNotNull(f)
        assertEquals(name, f.name)
        assertEquals(listOf("BRANCH"), f.vsNames)
    }

    @Test
    @Throws(Exception::class)
    fun by_name_none() {
        createFilters()
        val f = asUser().withView(branch).call { filterService.getValidationStampFilterByName(branch, uid("FX")).orElse(null) }
        assertNull(f)
    }

    @Test
    @Throws(Exception::class)
    fun save_global() {
        asUser().with(GlobalSettings::class.java).execute {
            var f = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(uid("F"))
                            .vsNames(listOf("GLOBAL"))
                            .build()
            )
            filterService.saveValidationStampFilter(f.withVsNames(Arrays.asList("GLOBAL", "ONTRACK")))
            f = filterService.getValidationStampFilter(f.id)
            assertEquals(Arrays.asList("GLOBAL", "ONTRACK"), f.vsNames)
        }
    }

    @Test
    @Throws(Exception::class)
    fun save_project() {
        asUser().with(branch, ProjectConfig::class.java).execute {
            var f = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(uid("F"))
                            .project(branch.project)
                            .vsNames(listOf("PROJECT"))
                            .build()
            )
            filterService.saveValidationStampFilter(f.withVsNames(Arrays.asList("PROJECT", "ONTRACK")))
            f = filterService.getValidationStampFilter(f.id)
            assertEquals(Arrays.asList("PROJECT", "ONTRACK"), f.vsNames)
        }
    }

    @Test
    @Throws(Exception::class)
    fun save_branch() {
        asUser().with(branch, ProjectConfig::class.java).execute {
            var f = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(uid("F"))
                            .branch(branch)
                            .vsNames(listOf("BRANCH"))
                            .build()
            )
            filterService.saveValidationStampFilter(f.withVsNames(Arrays.asList("BRANCH", "ONTRACK")))
            f = filterService.getValidationStampFilter(f.id)
            assertEquals(Arrays.asList("BRANCH", "ONTRACK"), f.vsNames)
        }
    }

    @Test
    @Throws(Exception::class)
    fun delete() {
        asUser().with(GlobalSettings::class.java).execute {
            val f = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(uid("F"))
                            .vsNames(listOf("GLOBAL"))
                            .build()
            )
            filterService.deleteValidationStampFilter(f)
            // Checks it is gone
            try {
                filterService.getValidationStampFilter(f.id)
                fail("It should have been deleted")
            } catch (ex: ValidationStampFilterNotFoundException) {
                assertEquals(String.format("Validation stamp filter with ID %s not found", f.id), ex.message)
            }
        }
    }

    @Test
    @Throws(Exception::class)
    fun share_from_branch_to_project() {
        asUser().with(branch, ProjectConfig::class.java).execute {
            val f = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(uid("F"))
                            .branch(branch)
                            .vsNames(listOf("CI"))
                            .build()
            )
            val f2 = filterService.shareValidationStampFilter(f, branch.project)
            assertTrue(f.id() == f2.id())
            assertNotNull(f2.project)
            assertNull(f2.branch)
        }
    }

    @Test
    @Throws(Exception::class)
    fun share_from_branch_to_global() {
        asUser().with(GlobalSettings::class.java).with(branch, ProjectConfig::class.java).execute {
            val f = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(uid("F"))
                            .branch(branch)
                            .vsNames(listOf("CI"))
                            .build()
            )
            val f2 = filterService.shareValidationStampFilter(f)
            assertTrue(f.id() == f2.id())
            assertNull(f2.project)
            assertNull(f2.branch)
        }
    }

    @Test
    @Throws(Exception::class)
    fun share_from_project_to_global() {
        asUser().with(GlobalSettings::class.java).with(branch, ProjectConfig::class.java).execute {
            val f = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(uid("F"))
                            .project(branch.project)
                            .vsNames(listOf("CI"))
                            .build()
            )
            val f2 = filterService.shareValidationStampFilter(f)
            assertTrue(f.id() == f2.id())
            assertNull(f2.project)
            assertNull(f2.branch)
        }
    }

    @Test
    @Throws(Exception::class)
    fun sharing_from_branch_to_project_remove_branch_filter() {
        asUser().with(branch, ProjectConfig::class.java).execute {
            val f = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(uid("F"))
                            .branch(branch)
                            .vsNames(listOf("BRANCH"))
                            .build()
            )
            // Shares at project level
            filterService.shareValidationStampFilter(f, branch.project)
            // Gets filters for the branch
            val filters = filterService.getBranchValidationStampFilters(branch, false)
            assertTrue("Branch has no longer any filter", filters.isEmpty())
        }
    }

    @Test
    @Throws(Exception::class)
    fun sharing_from_branch_to_global_remove_branch_filter() {
        asUser().with(branch, ProjectConfig::class.java).with(GlobalSettings::class.java).execute {
            val f = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(uid("F"))
                            .branch(branch)
                            .vsNames(listOf("BRANCH"))
                            .build()
            )
            // Shares at global level
            filterService.shareValidationStampFilter(f)
            // Gets filters for the branch
            val filters = filterService.getBranchValidationStampFilters(branch, false)
            assertTrue("Branch has no longer any filter", filters.isEmpty())
        }
    }

    @Test
    @Throws(Exception::class)
    fun sharing_from_project_to_global_remove_project_filter() {
        asUser().with(branch, ProjectConfig::class.java).with(GlobalSettings::class.java).execute {
            val f = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(uid("F"))
                            .project(branch.project)
                            .vsNames(listOf("PROJECT"))
                            .build()
            )
            // Shares at global level
            filterService.shareValidationStampFilter(f)
            // Gets filters for the project
            val filters = filterService.getProjectValidationStampFilters(branch.project, false)
            assertTrue("Project has no longer any filter", filters.isEmpty())
        }
    }

    @Test
    @Throws(Exception::class)
    fun null_patterns() {
        asUser().with(GlobalSettings::class.java).execute {
            var f = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(uid("F"))
                            .vsNames(null)
                            .build()
            )
            f = filterService.getValidationStampFilter(f.id)
            assertNotNull(f.vsNames)
            assertTrue(f.vsNames.isEmpty())
        }
    }

    @Test
    @Throws(Exception::class)
    fun empty_patterns() {
        asUser().with(GlobalSettings::class.java).execute {
            var f = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(uid("F"))
                            .vsNames(emptyList())
                            .build()
            )
            f = filterService.getValidationStampFilter(f.id)
            assertNotNull(f.vsNames)
            assertTrue(f.vsNames.isEmpty())
        }
    }

    // DSL

    private fun validationStampFilter(
            name: String = uid("VSF"),
            project: Project? = null,
            branch: Branch? = null,
            vsNames: List<String>
    ) = filterService.newValidationStampFilter(
            ValidationStampFilter.builder()
                    .name(name)
                    .project(project)
                    .branch(branch)
                    .vsNames(vsNames)
                    .build()
    )

}
