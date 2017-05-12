package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.exceptions.ValidationStampFilterNameAlreadyDefinedException;
import net.nemerosa.ontrack.model.exceptions.ValidationStampFilterNotFoundException;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.ValidationStampFilter;
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

public class ValidationStampFilterJdbcRepositoryIT extends AbstractRepositoryTestSupport {

    @Autowired
    private ValidationStampFilterRepository filterRepository;

    private Branch branch;

    @Before
    public void setup() {
        branch = do_create_branch();
    }

    @Test
    public void new_global_filter() {
        ValidationStampFilter filter = filterRepository.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name("My filter")
                        .vsNames(Collections.singletonList("CI"))
                        .build()
        );
        assertTrue(filter.getId().isSet());
    }

    @Test(expected = ValidationStampFilterNameAlreadyDefinedException.class)
    public void new_global_filter_with_existing_name() {
        filterRepository.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name("My filter")
                        .vsNames(Collections.singletonList("CI"))
                        .build()
        );
        // Creates another filter with the same name
        filterRepository.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name("My filter")
                        .vsNames(Collections.singletonList("OTHER"))
                        .build()
        );
    }

    @Test(expected = IllegalStateException.class)
    public void new_filter_with_both_project_and_branch() {
        Project project = Project.of(NameDescription.nd("P", ""));
        filterRepository.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name("My filter")
                        .project(project)
                        .branch(Branch.of(project, NameDescription.nd("B", "")))
                        .vsNames(Collections.singletonList("CI"))
                        .build()
        );
    }

    @Test
    public void new_project_filter() {
        ValidationStampFilter filter = filterRepository.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name("My filter")
                        .project(branch.getProject())
                        .vsNames(Collections.singletonList("CI"))
                        .build()
        );
        assertTrue(filter.getId().isSet());
    }

    @Test(expected = ValidationStampFilterNameAlreadyDefinedException.class)
    public void new_project_filter_with_existing_name() {
        filterRepository.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name("My filter")
                        .project(branch.getProject())
                        .vsNames(Collections.singletonList("CI"))
                        .build()
        );
        // Creates another filter with the same name
        filterRepository.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name("My filter")
                        .project(branch.getProject())
                        .vsNames(Collections.singletonList("OTHER"))
                        .build()
        );
    }

    @Test
    public void new_project_filter_with_same_name_than_global() {
        filterRepository.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name("My filter")
                        .vsNames(Collections.singletonList("CI"))
                        .build()
        );
        ValidationStampFilter filter = filterRepository.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name("My filter")
                        .project(branch.getProject())
                        .vsNames(Collections.singletonList("CI"))
                        .build()
        );
        assertTrue(filter.getId().isSet());
    }

    @Test
    public void new_branch_filter() {
        ValidationStampFilter filter = filterRepository.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name("My filter")
                        .branch(branch)
                        .vsNames(Collections.singletonList("CI"))
                        .build()
        );
        assertTrue(filter.getId().isSet());
    }

    @Test(expected = ValidationStampFilterNameAlreadyDefinedException.class)
    public void new_branch_filter_with_existing_name() {
        filterRepository.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name("My filter")
                        .branch(branch)
                        .vsNames(Collections.singletonList("CI"))
                        .build()
        );
        // Creates another filter with the same name
        filterRepository.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name("My filter")
                        .branch(branch)
                        .vsNames(Collections.singletonList("OTHER"))
                        .build()
        );
    }

    @Test
    public void new_branch_filter_with_same_name_than_global() {
        filterRepository.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name("My filter")
                        .vsNames(Collections.singletonList("CI"))
                        .build()
        );
        ValidationStampFilter filter = filterRepository.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name("My filter")
                        .branch(branch)
                        .vsNames(Collections.singletonList("CI"))
                        .build()
        );
        assertTrue(filter.getId().isSet());
    }

    @Test
    public void new_branch_filter_with_same_name_than_project() {
        filterRepository.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name("My filter")
                        .project(branch.getProject())
                        .vsNames(Collections.singletonList("CI"))
                        .build()
        );
        ValidationStampFilter filter = filterRepository.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name("My filter")
                        .branch(branch)
                        .vsNames(Collections.singletonList("CI"))
                        .build()
        );
        assertTrue(filter.getId().isSet());
    }

    private String createFilters() {
        String name = uid("F");
        // Global
        filterRepository.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name(name)
                        .vsNames(Collections.singletonList("GLOBAL"))
                        .build()
        );
        // Project
        filterRepository.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name(name)
                        .project(branch.getProject())
                        .vsNames(Collections.singletonList("PROJECT"))
                        .build()
        );
        // Branch
        filterRepository.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name(name)
                        .branch(branch)
                        .vsNames(Collections.singletonList("BRANCH"))
                        .build()
        );
        // OK
        return name;
    }

    @Test
    public void global_filters() {
        String name = createFilters();
        List<ValidationStampFilter> list = filterRepository.getGlobalValidationStampFilters().stream()
                .filter(f -> StringUtils.equals(name, f.getName()))
                .collect(Collectors.toList());
        assertEquals(1, list.size());
        assertEquals(name, list.get(0).getName());
        assertEquals(Collections.singletonList("GLOBAL"), list.get(0).getVsNames());
    }

    @Test
    public void project_filters() {
        String name = createFilters();
        List<ValidationStampFilter> list = filterRepository.getProjectValidationStampFilters(branch.getProject()).stream()
                .filter(f -> StringUtils.equals(name, f.getName()))
                .collect(Collectors.toList());
        assertEquals(1, list.size());
        assertEquals(name, list.get(0).getName());
        assertEquals(Collections.singletonList("PROJECT"), list.get(0).getVsNames());
    }

    @Test
    public void branch_filters() {
        String name = createFilters();
        List<ValidationStampFilter> list = filterRepository.getBranchValidationStampFilters(branch).stream()
                .filter(f -> StringUtils.equals(name, f.getName()))
                .collect(Collectors.toList());
        assertEquals(1, list.size());
        assertEquals(name, list.get(0).getName());
        assertEquals(Collections.singletonList("BRANCH"), list.get(0).getVsNames());
    }

    @Test
    public void by_name_global_only() {
        String name = uid("F");
        filterRepository.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name(name)
                        .vsNames(Collections.singletonList("GLOBAL"))
                        .build()
        );
        ValidationStampFilter f = filterRepository.getValidationStampFilterByName(branch, name).orElse(null);
        assertNotNull(f);
        assertEquals(name, f.getName());
        assertEquals(Collections.singletonList("GLOBAL"), f.getVsNames());
    }

    @Test
    public void by_name_global_and_project() {
        String name = uid("F");
        filterRepository.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name(name)
                        .vsNames(Collections.singletonList("GLOBAL"))
                        .build()
        );
        filterRepository.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name(name)
                        .project(branch.getProject())
                        .vsNames(Collections.singletonList("PROJECT"))
                        .build()
        );
        ValidationStampFilter f = filterRepository.getValidationStampFilterByName(branch, name).orElse(null);
        assertNotNull(f);
        assertEquals(name, f.getName());
        assertEquals(Collections.singletonList("PROJECT"), f.getVsNames());
    }

    @Test
    public void by_name_branch() {
        String name = uid("F");
        filterRepository.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name(name)
                        .vsNames(Collections.singletonList("GLOBAL"))
                        .build()
        );
        filterRepository.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name(name)
                        .project(branch.getProject())
                        .vsNames(Collections.singletonList("PROJECT"))
                        .build()
        );
        filterRepository.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name(name)
                        .branch(branch)
                        .vsNames(Collections.singletonList("BRANCH"))
                        .build()
        );
        ValidationStampFilter f = filterRepository.getValidationStampFilterByName(branch, name).orElse(null);
        assertNotNull(f);
        assertEquals(name, f.getName());
        assertEquals(Collections.singletonList("BRANCH"), f.getVsNames());
    }

    @Test
    public void by_name_none() {
        createFilters();
        ValidationStampFilter f = filterRepository.getValidationStampFilterByName(branch, uid("FX")).orElse(null);
        assertNull(f);
    }

    @Test
    public void save_global() {
        ValidationStampFilter f = filterRepository.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name(uid("F"))
                        .vsNames(Collections.singletonList("GLOBAL"))
                        .build()
        );
        filterRepository.saveValidationStampFilter(f.withVsNames(Arrays.asList("GLOBAL", "ONTRACK")));
        f = filterRepository.getValidationStampFilter(f.getId());
        assertEquals(Arrays.asList("GLOBAL", "ONTRACK"), f.getVsNames());
    }

    @Test
    public void save_project() {
        ValidationStampFilter f = filterRepository.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name(uid("F"))
                        .project(branch.getProject())
                        .vsNames(Collections.singletonList("PROJECT"))
                        .build()
        );
        filterRepository.saveValidationStampFilter(f.withVsNames(Arrays.asList("PROJECT", "ONTRACK")));
        f = filterRepository.getValidationStampFilter(f.getId());
        assertEquals(Arrays.asList("PROJECT", "ONTRACK"), f.getVsNames());
    }

    @Test
    public void save_branch() {
        ValidationStampFilter f = filterRepository.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name(uid("F"))
                        .branch(branch)
                        .vsNames(Collections.singletonList("BRANCH"))
                        .build()
        );
        filterRepository.saveValidationStampFilter(f.withVsNames(Arrays.asList("BRANCH", "ONTRACK")));
        f = filterRepository.getValidationStampFilter(f.getId());
        assertEquals(Arrays.asList("BRANCH", "ONTRACK"), f.getVsNames());
    }

    @Test
    public void delete() {
        ValidationStampFilter f = filterRepository.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name(uid("F"))
                        .vsNames(Collections.singletonList("GLOBAL"))
                        .build()
        );
        filterRepository.deleteValidationStampFilter(f.getId());
        // Checks it is gone
        try {
            filterRepository.getValidationStampFilter(f.getId());
            fail("It should have been deleted");
        } catch (ValidationStampFilterNotFoundException ex) {
            assertEquals(String.format("Validation stamp filter with ID %s not found", f.getId()), ex.getMessage());
        }
    }

    @Test
    public void share_from_branch_to_project() {
        ValidationStampFilter f = filterRepository.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name(uid("F"))
                        .branch(branch)
                        .vsNames(Collections.singletonList("CI"))
                        .build()
        );
        ValidationStampFilter f2 = filterRepository.shareValidationStampFilter(f, branch.getProject());
        assertTrue(f.id() == f2.id());
        assertNotNull(f2.getProject());
        assertNull(f2.getBranch());
    }

    @Test
    public void share_from_branch_to_global() {
        ValidationStampFilter f = filterRepository.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name(uid("F"))
                        .branch(branch)
                        .vsNames(Collections.singletonList("CI"))
                        .build()
        );
        ValidationStampFilter f2 = filterRepository.shareValidationStampFilter(f);
        assertTrue(f.id() == f2.id());
        assertNull(f2.getProject());
        assertNull(f2.getBranch());
    }

    @Test
    public void share_from_project_to_global() {
        ValidationStampFilter f = filterRepository.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name(uid("F"))
                        .project(branch.getProject())
                        .vsNames(Collections.singletonList("CI"))
                        .build()
        );
        ValidationStampFilter f2 = filterRepository.shareValidationStampFilter(f);
        assertTrue(f.id() == f2.id());
        assertNull(f2.getProject());
        assertNull(f2.getBranch());
    }

    @Test
    public void null_patterns() {
        ValidationStampFilter f = filterRepository.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name(uid("F"))
                        .vsNames(null)
                        .build()
        );
        f = filterRepository.getValidationStampFilter(f.getId());
        assertNotNull(f.getVsNames());
        assertTrue(f.getVsNames().isEmpty());
    }

    @Test
    public void empty_patterns() {
        ValidationStampFilter f = filterRepository.newValidationStampFilter(
                ValidationStampFilter.builder()
                        .name(uid("F"))
                        .vsNames(Collections.emptyList())
                        .build()
        );
        f = filterRepository.getValidationStampFilter(f.getId());
        assertNotNull(f.getVsNames());
        assertTrue(f.getVsNames().isEmpty());
    }

}
