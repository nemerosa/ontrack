package net.nemerosa.ontrack.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterResult;
import net.nemerosa.ontrack.model.structure.*;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static net.nemerosa.ontrack.json.JsonUtils.object;
import static net.nemerosa.ontrack.test.TestUtils.assertJsonRead;
import static org.junit.Assert.*;

public class StandardBuildFilterTest {

    private Branch branch;
    private Build build;

    @Before
    public void prepare() {
        Project project = Project.of(new NameDescription("P", "Project")).withId(ID.of(1));
        branch = Branch.of(project, new NameDescription("B", "Branch")).withId(ID.of(1));
        build = Build.of(
                branch,
                new NameDescription("1", "Build 1"),
                Signature.of("user").withTime(LocalDateTime.of(2014, 7, 14, 13, 25, 0))
        );
    }

    @Test
    public void afterDate_nok() {
        StandardBuildFilter filter = new StandardBuildFilter(
                StandardBuildFilterData.of(5).withAfterDate(LocalDate.of(2014, 7, 16))
        );
        BuildFilterResult result = filter.filter(
                Collections.emptyList(),
                branch,
                build,
                () -> null
        );
        assertNotNull(result);
        assertTrue(result.isGoingOn());
        assertFalse(result.isAccept());
    }

    @Test
    public void afterDate_ok() {
        StandardBuildFilter filter = new StandardBuildFilter(
                StandardBuildFilterData.of(5).withAfterDate(LocalDate.of(2014, 7, 12))
        );
        BuildFilterResult result = filter.filter(
                Collections.emptyList(),
                branch,
                build,
                () -> null
        );
        assertNotNull(result);
        assertTrue(result.isGoingOn());
        assertTrue(result.isAccept());
    }

    @Test
    public void afterDate_same() {
        StandardBuildFilter filter = new StandardBuildFilter(
                StandardBuildFilterData.of(5).withAfterDate(LocalDate.of(2014, 7, 14))
        );
        BuildFilterResult result = filter.filter(
                Collections.emptyList(),
                branch,
                build,
                () -> null
        );
        assertNotNull(result);
        assertTrue(result.isGoingOn());
        assertTrue(result.isAccept());
    }

    @Test
    public void beforeDate_nok() {
        StandardBuildFilter filter = new StandardBuildFilter(
                StandardBuildFilterData.of(5).withBeforeDate(LocalDate.of(2014, 7, 12))
        );
        BuildFilterResult result = filter.filter(
                Collections.emptyList(),
                branch,
                build,
                () -> null
        );
        assertNotNull(result);
        assertTrue(result.isGoingOn());
        assertFalse(result.isAccept());
    }

    @Test
    public void beforeDate_ok() {
        StandardBuildFilter filter = new StandardBuildFilter(
                StandardBuildFilterData.of(5).withBeforeDate(LocalDate.of(2014, 7, 16))
        );
        BuildFilterResult result = filter.filter(
                Collections.emptyList(),
                branch,
                build,
                () -> null
        );
        assertNotNull(result);
        assertTrue(result.isGoingOn());
        assertTrue(result.isAccept());
    }

    @Test
    public void beforeDate_same() {
        StandardBuildFilter filter = new StandardBuildFilter(
                StandardBuildFilterData.of(5).withAfterDate(LocalDate.of(2014, 7, 14))
        );
        BuildFilterResult result = filter.filter(
                Collections.emptyList(),
                branch,
                build,
                () -> null
        );
        assertNotNull(result);
        assertTrue(result.isGoingOn());
        assertTrue(result.isAccept());
    }

    @Test
    public void json_to_afterDate() throws JsonProcessingException {
        assertJsonRead(
                StandardBuildFilterData.of(2).withAfterDate(LocalDate.of(2014, 7, 14)),
                object()
                        .with("count", "2")
                        .with("afterDate", "2014-07-14")
                        .end(),
                StandardBuildFilterData.class
        );
    }

}