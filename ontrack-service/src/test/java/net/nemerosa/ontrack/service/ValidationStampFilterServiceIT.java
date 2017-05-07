package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.it.AbstractServiceTestSupport;
import net.nemerosa.ontrack.model.exceptions.ValidationStampFilterNameAlreadyDefinedException;
import net.nemerosa.ontrack.model.exceptions.ValidationStampFilterNotFoundException;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.ValidationStampFilter;
import net.nemerosa.ontrack.model.structure.ValidationStampFilterService;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static net.nemerosa.ontrack.test.TestUtils.uid;
import static org.junit.Assert.*;

public class ValidationStampFilterServiceIT extends AbstractServiceTestSupport {

    @Autowired
    private ValidationStampFilterService filterService;

    private Branch branch;

    @Before
    public void setup() throws Exception {
        branch = doCreateBranch();
    }

    @Test
    public void new_global_filter() throws Exception {
        asUser().with(GlobalSettings.class).execute(() -> {
            ValidationStampFilter filter = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name("My filter")
                            .vsNames(Collections.singletonList("CI"))
                            .build()
            );
            assertTrue(filter.getId().isSet());
        });
    }

    @Test(expected = ValidationStampFilterNameAlreadyDefinedException.class)
    public void new_global_filter_with_existing_name() throws Exception {
        asUser().with(GlobalSettings.class).execute(() -> {
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name("My filter")
                            .vsNames(Collections.singletonList("CI"))
                            .build()
            );
            // Creates another filter with the same name
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name("My filter")
                            .vsNames(Collections.singletonList("OTHER"))
                            .build()
            );
        });
    }

    @Test(expected = IllegalStateException.class)
    public void new_filter_with_both_project_and_branch() throws Exception {
        asUser().with(branch, ProjectConfig.class).execute(() -> filterService.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name("My filter")
                        .project(branch.getProject())
                        .branch(branch)
                        .vsNames(Collections.singletonList("CI"))
                        .build()
        ));
    }

    @Test
    public void new_project_filter() throws Exception {
        asUser().with(branch, ProjectConfig.class).execute(() -> {
            ValidationStampFilter filter = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name("My filter")
                            .project(branch.getProject())
                            .vsNames(Collections.singletonList("CI"))
                            .build()
            );
            assertTrue(filter.getId().isSet());
        });
    }

    @Test(expected = ValidationStampFilterNameAlreadyDefinedException.class)
    public void new_project_filter_with_existing_name() throws Exception {
        asUser().with(branch, ProjectConfig.class).execute(() -> {
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name("My filter")
                            .project(branch.getProject())
                            .vsNames(Collections.singletonList("CI"))
                            .build()
            );
            // Creates another filter with the same name
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name("My filter")
                            .project(branch.getProject())
                            .vsNames(Collections.singletonList("OTHER"))
                            .build()
            );
        });
    }

    @Test
    public void new_project_filter_with_same_name_than_global() throws Exception {
        asUser().with(GlobalSettings.class).with(branch, ProjectConfig.class).execute(() -> {
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name("My filter")
                            .vsNames(Collections.singletonList("CI"))
                            .build()
            );
            ValidationStampFilter filter = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name("My filter")
                            .project(branch.getProject())
                            .vsNames(Collections.singletonList("CI"))
                            .build()
            );
            assertTrue(filter.getId().isSet());
        });
    }

    @Test
    public void new_branch_filter() throws Exception {
        asUser().with(branch, ProjectConfig.class).execute(() -> {
            ValidationStampFilter filter = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name("My filter")
                            .branch(branch)
                            .vsNames(Collections.singletonList("CI"))
                            .build()
            );
            assertTrue(filter.getId().isSet());
        });
    }

    @Test(expected = ValidationStampFilterNameAlreadyDefinedException.class)
    public void new_branch_filter_with_existing_name() throws Exception {
        asUser().with(branch, ProjectConfig.class).execute(() -> {
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name("My filter")
                            .branch(branch)
                            .vsNames(Collections.singletonList("CI"))
                            .build()
            );
            // Creates another filter with the same name
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name("My filter")
                            .branch(branch)
                            .vsNames(Collections.singletonList("OTHER"))
                            .build()
            );
        });
    }

    @Test
    public void new_branch_filter_with_same_name_than_global() throws Exception {
        asUser().with(GlobalSettings.class).with(branch, ProjectConfig.class).execute(() -> {
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name("My filter")
                            .vsNames(Collections.singletonList("CI"))
                            .build()
            );
            ValidationStampFilter filter = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name("My filter")
                            .branch(branch)
                            .vsNames(Collections.singletonList("CI"))
                            .build()
            );
            assertTrue(filter.getId().isSet());
        });
    }

    @Test
    public void new_branch_filter_with_same_name_than_project() throws Exception {
        asUser().with(GlobalSettings.class).with(branch, ProjectConfig.class).execute(() -> {
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name("My filter")
                            .project(branch.getProject())
                            .vsNames(Collections.singletonList("CI"))
                            .build()
            );
            ValidationStampFilter filter = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name("My filter")
                            .branch(branch)
                            .vsNames(Collections.singletonList("CI"))
                            .build()
            );
            assertTrue(filter.getId().isSet());
        });
    }

    @SuppressWarnings("Duplicates")
    private String createFilters() throws Exception {
        return asUser().with(GlobalSettings.class).with(branch, ProjectConfig.class).call(() -> {
            String name = uid("F");
            // Global
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(name)
                            .vsNames(Collections.singletonList("GLOBAL"))
                            .build()
            );
            // Project
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(name)
                            .project(branch.getProject())
                            .vsNames(Collections.singletonList("PROJECT"))
                            .build()
            );
            // Branch
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(name)
                            .branch(branch)
                            .vsNames(Collections.singletonList("BRANCH"))
                            .build()
            );
            // OK
            return name;
        });
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void global_filters() throws Exception {
        String name = createFilters();
        asUser().with(GlobalSettings.class).execute(() -> {
            List<ValidationStampFilter> list = filterService.getGlobalValidationStampFilters().stream()
                    .filter(f -> StringUtils.equals(name, f.getName()))
                    .collect(Collectors.toList());
            assertEquals(1, list.size());
            assertEquals(name, list.get(0).getName());
            assertEquals(Collections.singletonList("GLOBAL"), list.get(0).getVsNames());
        });
    }

    @Test
    public void project_filters_only() throws Exception {
        String name = createFilters();
        List<ValidationStampFilter> list = asUser().withView(branch).call(() -> filterService.getProjectValidationStampFilters(branch.getProject(), false).stream()
                .filter(f -> StringUtils.equals(name, f.getName()))
                .collect(Collectors.toList())
        );
        assertEquals(1, list.size());
        assertEquals(name, list.get(0).getName());
        assertEquals(Collections.singletonList("PROJECT"), list.get(0).getVsNames());
    }

    @Test
    public void project_filters_include_all() throws Exception {
        String name = createFilters();
        List<ValidationStampFilter> list = asUser().withView(branch).call(() -> filterService.getProjectValidationStampFilters(branch.getProject(), true).stream()
                .filter(f -> StringUtils.equals(name, f.getName()))
                .collect(Collectors.toList())
        );
        assertEquals(1, list.size());
        assertEquals(name, list.get(0).getName());
        assertEquals(Collections.singletonList("PROJECT"), list.get(0).getVsNames());
    }

    @Test
    public void branch_filters_only() throws Exception {
        String name = createFilters();
        List<ValidationStampFilter> list = asUser().withView(branch).call(() -> filterService.getBranchValidationStampFilters(branch, false).stream()
                .filter(f -> StringUtils.equals(name, f.getName()))
                .collect(Collectors.toList())
        );
        assertEquals(1, list.size());
        assertEquals(name, list.get(0).getName());
        assertEquals(Collections.singletonList("BRANCH"), list.get(0).getVsNames());
    }

    @Test
    public void branch_filters_include_all() throws Exception {
        String name = createFilters();
        List<ValidationStampFilter> list = asUser().withView(branch).call(() -> filterService.getBranchValidationStampFilters(branch, true).stream()
                .filter(f -> StringUtils.equals(name, f.getName()))
                .collect(Collectors.toList())
        );
        assertEquals(1, list.size());
        assertEquals(name, list.get(0).getName());
        assertEquals(Collections.singletonList("BRANCH"), list.get(0).getVsNames());
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void by_name_global_only() throws Exception {
        String name = uid("F");
        asUser().with(GlobalSettings.class).execute(() -> filterService.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name(name)
                        .vsNames(Collections.singletonList("GLOBAL"))
                        .build()
        ));
        ValidationStampFilter f = asUser().withView(branch).call(() -> filterService.getValidationStampFilterByName(branch, name).orElse(null));
        assertNotNull(f);
        assertEquals(name, f.getName());
        assertEquals(Collections.singletonList("GLOBAL"), f.getVsNames());
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void by_name_global_and_project() throws Exception {
        String name = uid("F");
        asUser().with(GlobalSettings.class).with(branch, ProjectConfig.class).execute(() -> {
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(name)
                            .vsNames(Collections.singletonList("GLOBAL"))
                            .build()
            );
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(name)
                            .project(branch.getProject())
                            .vsNames(Collections.singletonList("PROJECT"))
                            .build()
            );
        });
        ValidationStampFilter f = asUser().withView(branch).call(() -> filterService.getValidationStampFilterByName(branch, name).orElse(null));
        assertNotNull(f);
        assertEquals(name, f.getName());
        assertEquals(Collections.singletonList("PROJECT"), f.getVsNames());
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void by_name_branch() throws Exception {
        String name = uid("F");
        asUser().with(GlobalSettings.class).with(branch, ProjectConfig.class).execute(() -> {
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(name)
                            .vsNames(Collections.singletonList("GLOBAL"))
                            .build()
            );
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(name)
                            .project(branch.getProject())
                            .vsNames(Collections.singletonList("PROJECT"))
                            .build()
            );
            filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(name)
                            .branch(branch)
                            .vsNames(Collections.singletonList("BRANCH"))
                            .build()
            );
        });
        ValidationStampFilter f = asUser().withView(branch).call(() -> filterService.getValidationStampFilterByName(branch, name).orElse(null));
        assertNotNull(f);
        assertEquals(name, f.getName());
        assertEquals(Collections.singletonList("BRANCH"), f.getVsNames());
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void by_name_none() throws Exception {
        createFilters();
        ValidationStampFilter f = asUser().withView(branch).call(() -> filterService.getValidationStampFilterByName(branch, uid("FX")).orElse(null));
        assertNull(f);
    }

    @Test
    public void save_global() throws Exception {
        asUser().with(GlobalSettings.class).execute(() -> {
            ValidationStampFilter f = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(uid("F"))
                            .vsNames(Collections.singletonList("GLOBAL"))
                            .build()
            );
            filterService.saveValidationStampFilter(f.withVsNames(Arrays.asList("GLOBAL", "ONTRACK")));
            f = filterService.getValidationStampFilter(f.getId());
            assertEquals(Arrays.asList("GLOBAL", "ONTRACK"), f.getVsNames());
        });
    }

    @Test
    public void save_project() throws Exception {
        asUser().with(branch, ProjectConfig.class).execute(() -> {
            ValidationStampFilter f = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(uid("F"))
                            .project(branch.getProject())
                            .vsNames(Collections.singletonList("PROJECT"))
                            .build()
            );
            filterService.saveValidationStampFilter(f.withVsNames(Arrays.asList("PROJECT", "ONTRACK")));
            f = filterService.getValidationStampFilter(f.getId());
            assertEquals(Arrays.asList("PROJECT", "ONTRACK"), f.getVsNames());
        });
    }

    @Test
    public void save_branch() throws Exception {
        asUser().with(branch, ProjectConfig.class).execute(() -> {
            ValidationStampFilter f = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(uid("F"))
                            .branch(branch)
                            .vsNames(Collections.singletonList("BRANCH"))
                            .build()
            );
            filterService.saveValidationStampFilter(f.withVsNames(Arrays.asList("BRANCH", "ONTRACK")));
            f = filterService.getValidationStampFilter(f.getId());
            assertEquals(Arrays.asList("BRANCH", "ONTRACK"), f.getVsNames());
        });
    }

    @Test
    public void delete() throws Exception {
        asUser().with(GlobalSettings.class).execute(() -> {
            ValidationStampFilter f = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(uid("F"))
                            .vsNames(Collections.singletonList("GLOBAL"))
                            .build()
            );
            filterService.deleteValidationStampFilter(f);
            // Checks it is gone
            try {
                filterService.getValidationStampFilter(f.getId());
                fail("It should have been deleted");
            } catch (ValidationStampFilterNotFoundException ex) {
                assertEquals(String.format("Validation stamp filter with ID %s not found", f.getId()), ex.getMessage());
            }
        });
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void share_from_branch_to_project() throws Exception {
        asUser().with(branch, ProjectConfig.class).execute(() -> {
            ValidationStampFilter f = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(uid("F"))
                            .branch(branch)
                            .vsNames(Collections.singletonList("CI"))
                            .build()
            );
            ValidationStampFilter f2 = filterService.shareValidationStampFilter(f, branch.getProject());
            assertTrue(f.id() == f2.id());
            assertNotNull(f2.getProject());
            assertNull(f2.getBranch());
        });
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void share_from_branch_to_global() throws Exception {
        asUser().with(GlobalSettings.class).with(branch, ProjectConfig.class).execute(() -> {
            ValidationStampFilter f = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(uid("F"))
                            .branch(branch)
                            .vsNames(Collections.singletonList("CI"))
                            .build()
            );
            ValidationStampFilter f2 = filterService.shareValidationStampFilter(f);
            assertTrue(f.id() == f2.id());
            assertNull(f2.getProject());
            assertNull(f2.getBranch());
        });
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void share_from_project_to_global() throws Exception {
        asUser().with(GlobalSettings.class).with(branch, ProjectConfig.class).execute(() -> {
            ValidationStampFilter f = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(uid("F"))
                            .project(branch.getProject())
                            .vsNames(Collections.singletonList("CI"))
                            .build()
            );
            ValidationStampFilter f2 = filterService.shareValidationStampFilter(f);
            assertTrue(f.id() == f2.id());
            assertNull(f2.getProject());
            assertNull(f2.getBranch());
        });
    }

    @Test
    public void sharing_from_branch_to_project_remove_branch_filter() throws Exception {
        asUser().with(branch, ProjectConfig.class).execute(() -> {
            ValidationStampFilter f = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(uid("F"))
                            .branch(branch)
                            .vsNames(Collections.singletonList("BRANCH"))
                            .build()
            );
            // Shares at project level
            filterService.shareValidationStampFilter(f, branch.getProject());
            // Gets filters for the branch
            List<ValidationStampFilter> filters = filterService.getBranchValidationStampFilters(branch, false);
            assertTrue("Branch has no longer any filter", filters.isEmpty());
        });
    }

    @Test
    public void sharing_from_branch_to_global_remove_branch_filter() throws Exception {
        asUser().with(branch, ProjectConfig.class).with(GlobalSettings.class).execute(() -> {
            ValidationStampFilter f = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(uid("F"))
                            .branch(branch)
                            .vsNames(Collections.singletonList("BRANCH"))
                            .build()
            );
            // Shares at global level
            filterService.shareValidationStampFilter(f);
            // Gets filters for the branch
            List<ValidationStampFilter> filters = filterService.getBranchValidationStampFilters(branch, false);
            assertTrue("Branch has no longer any filter", filters.isEmpty());
        });
    }

    @Test
    public void sharing_from_project_to_global_remove_project_filter() throws Exception {
        asUser().with(branch, ProjectConfig.class).with(GlobalSettings.class).execute(() -> {
            ValidationStampFilter f = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(uid("F"))
                            .project(branch.getProject())
                            .vsNames(Collections.singletonList("PROJECT"))
                            .build()
            );
            // Shares at global level
            filterService.shareValidationStampFilter(f);
            // Gets filters for the project
            List<ValidationStampFilter> filters = filterService.getProjectValidationStampFilters(branch.getProject(), false);
            assertTrue("Project has no longer any filter", filters.isEmpty());
        });
    }

    @Test
    public void null_patterns() throws Exception {
        asUser().with(GlobalSettings.class).execute(() -> {
            ValidationStampFilter f = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(uid("F"))
                            .vsNames(null)
                            .build()
            );
            f = filterService.getValidationStampFilter(f.getId());
            assertNotNull(f.getVsNames());
            assertTrue(f.getVsNames().isEmpty());
        });
    }

    @Test
    public void empty_patterns() throws Exception {
        asUser().with(GlobalSettings.class).execute(() -> {
            ValidationStampFilter f = filterService.newValidationStampFilter(
                    ValidationStampFilter.builder()
                            .name(uid("F"))
                            .vsNames(Collections.emptyList())
                            .build()
            );
            f = filterService.getValidationStampFilter(f.getId());
            assertNotNull(f.getVsNames());
            assertTrue(f.getVsNames().isEmpty());
        });
    }

}
