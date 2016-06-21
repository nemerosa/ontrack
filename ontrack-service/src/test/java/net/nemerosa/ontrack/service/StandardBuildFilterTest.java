package net.nemerosa.ontrack.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterResult;
import net.nemerosa.ontrack.model.structure.*;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static net.nemerosa.ontrack.json.JsonUtils.object;
import static net.nemerosa.ontrack.test.TestUtils.assertJsonRead;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class StandardBuildFilterTest {

    private Branch branch;
    private Build build;
    private PropertyService propertyService;
    private StructureService structureService;
    private PromotionLevel copper;
    private PromotionLevel bronze;

    @Before
    public void prepare() {
        Project project = Project.of(new NameDescription("P", "Project")).withId(ID.of(1));
        branch = Branch.of(project, new NameDescription("B", "Branch")).withId(ID.of(1));
        build = Build.of(
                branch,
                new NameDescription("1", "Build 1"),
                Signature.of("user").withTime(LocalDateTime.of(2014, 7, 14, 13, 25, 0))
        );
        copper = PromotionLevel.of(branch, new NameDescription("COPPER", ""));
        bronze = PromotionLevel.of(branch, new NameDescription("BRONZE", ""));
        propertyService = mock(PropertyService.class);
        structureService = mock(StructureService.class);
    }

    /**
     * Tests the following sequence:
     * <p>
     * <pre>
     *     1
     *     2
     *     3
     *     4 --> COPPER
     *     5 --> BRONZE
     * </pre>
     * <p>
     * Builds 1 to 3 should be accepted:
     * <ul>
     * <li>Since promotion level: COPPER</li>
     * </ul>
     */
    @Test
    public void since_promotion_level__none_before_is_accepted() {
        StandardBuildFilter filter = new StandardBuildFilter(
                StandardBuildFilterData.of(5).withSincePromotionLevel("COPPER"),
                propertyService,
                structureService
        );
        BuildFilterResult result = filter.filter(
                Collections.emptyList(),
                branch,
                build,
                () -> BuildView.of(build)
        );
        assertNotNull(result);
        assertTrue(result.isGoingOn());
        assertTrue(result.isAccept());
    }

    /**
     * Tests the following sequence:
     * <p>
     * <pre>
     *     1
     *     2 --> COPPER
     *     3
     *     4 --> COPPER
     *     5 --> COPPER, BRONZE
     * </pre>
     * <p>
     * Build 5 should be accepted and no further build should be scan for:
     * <ul>
     * <li>With promotion level: COPPER</li>
     * <li>Since promotion level: BRONZE</li>
     * </ul>
     */
    @Test
    public void with_since_promotion_level__last_one_accepted() {
        StandardBuildFilter filter = new StandardBuildFilter(
                StandardBuildFilterData.of(5).withSincePromotionLevel("BRONZE").withWithPromotionLevel("COPPER"),
                propertyService,
                structureService
        );
        BuildFilterResult result = filter.filter(
                Collections.emptyList(),
                branch,
                build,
                () -> BuildView.of(build)
                        .withPromotionRuns(
                                Arrays.asList(
                                        PromotionRun.of(
                                                build,
                                                copper,
                                                Signature.of("test"),
                                                ""
                                        ),
                                        PromotionRun.of(
                                                build,
                                                bronze,
                                                Signature.of("test"),
                                                ""
                                        )

                                )
                        )
        );
        assertNotNull(result);
        assertFalse("Not going on after Bronze build", result.isGoingOn());
        assertTrue("Last copper/bronze build accepted", result.isAccept());
    }

    /**
     * Tests the following sequence:
     * <p>
     * <pre>
     *     1
     *     2 --> COPPER
     *     3
     *     4 --> COPPER
     *     5 --> BRONZE
     * </pre>
     * <p>
     * Build 5 should be accepted and no further build should be scan for:
     * <ul>
     * <li>With promotion level: COPPER</li>
     * <li>Since promotion level: BRONZE</li>
     * </ul>
     */
    @Test
    public void with_since_promotion_level__last_one_accepted_when_not_promoted() {
        StandardBuildFilter filter = new StandardBuildFilter(
                StandardBuildFilterData.of(5).withSincePromotionLevel("BRONZE").withWithPromotionLevel("COPPER"),
                propertyService,
                structureService
        );
        BuildFilterResult result = filter.filter(
                Collections.emptyList(),
                branch,
                build,
                () -> BuildView.of(build)
                        .withPromotionRuns(
                                Collections.singletonList(
                                        PromotionRun.of(
                                                build,
                                                bronze,
                                                Signature.of("test"),
                                                ""
                                        )
                                )
                        )
        );
        assertNotNull(result);
        assertFalse("Not going on after Bronze build", result.isGoingOn());
        assertTrue("Last bronze build accepted", result.isAccept());
    }

    @Test
    public void afterDate_nok() {
        StandardBuildFilter filter = new StandardBuildFilter(
                StandardBuildFilterData.of(5).withAfterDate(LocalDate.of(2014, 7, 16)),
                propertyService,
                structureService
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
                StandardBuildFilterData.of(5).withAfterDate(LocalDate.of(2014, 7, 12)),
                propertyService,
                structureService
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
                StandardBuildFilterData.of(5).withAfterDate(LocalDate.of(2014, 7, 14)),
                propertyService,
                structureService
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
                StandardBuildFilterData.of(5).withBeforeDate(LocalDate.of(2014, 7, 12)),
                propertyService,
                structureService
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
                StandardBuildFilterData.of(5).withBeforeDate(LocalDate.of(2014, 7, 16)),
                propertyService,
                structureService
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
                StandardBuildFilterData.of(5).withBeforeDate(LocalDate.of(2014, 7, 14)),
                propertyService,
                structureService
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