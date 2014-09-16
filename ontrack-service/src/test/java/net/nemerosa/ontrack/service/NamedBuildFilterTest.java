package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.buildfilter.BuildFilterResult;
import net.nemerosa.ontrack.model.structure.*;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class NamedBuildFilterTest {

    private Branch branch;
    private Build build100;
    private Build build101;
    private Build build110;
    private Build build111;
    private Build build120;
    private PromotionLevel copper;

    @Before
    public void prepare() {
        Project project = Project.of(new NameDescription("P", "Project")).withId(ID.of(1));
        branch = Branch.of(project, new NameDescription("B", "Branch")).withId(ID.of(1));
        build100 = Build.of(
                branch,
                new NameDescription("100", "Build 100"),
                Signature.of("user").withTime(LocalDateTime.of(2014, 7, 14, 13, 25, 0))
        ).withId(ID.of(100));
        build101 = Build.of(
                branch,
                new NameDescription("101", "Build 101"),
                Signature.of("user").withTime(LocalDateTime.of(2014, 7, 15, 13, 25, 0))
        ).withId(ID.of(101));
        build110 = Build.of(
                branch,
                new NameDescription("110", "Build 110"),
                Signature.of("user").withTime(LocalDateTime.of(2014, 7, 16, 13, 25, 0))
        ).withId(ID.of(110));
        build111 = Build.of(
                branch,
                new NameDescription("111", "Build 111"),
                Signature.of("user").withTime(LocalDateTime.of(2014, 7, 17, 13, 25, 0))
        ).withId(ID.of(111));
        build120 = Build.of(
                branch,
                new NameDescription("120", "Build 120"),
                Signature.of("user").withTime(LocalDateTime.of(2014, 7, 18, 13, 25, 0))
        ).withId(ID.of(120));
        copper = PromotionLevel.of(branch, new NameDescription("COPPER", ""));
    }

    /**
     * Tests the following sequence:
     * <p>
     * <pre>
     *     120
     *     111
     *     110
     *     101
     *     100
     * </pre>
     * <p>
     * Builds 111 and 101 should be accepted:
     * <ul>
     * <li>From build: 11.*</li>
     * </ul>
     */
    @Test
    public void from_build() {
        NamedBuildFilter filter = new NamedBuildFilter(
                NamedBuildFilterData.of("11.*")
        );
        {
            BuildFilterResult result = filterNoPromotion(filter, build120);
            assertNotNull(result);
            assertFalse(result.isAccept());
            assertTrue(result.isGoingOn());
        }
        {
            BuildFilterResult result = filterNoPromotion(filter, build111);
            assertNotNull(result);
            assertTrue(result.isAccept());
            assertTrue(result.isGoingOn());
        }
        {
            BuildFilterResult result = filterNoPromotion(filter, build110);
            assertNotNull(result);
            assertFalse(result.isAccept());
            assertTrue(result.isGoingOn());
        }
        {
            BuildFilterResult result = filterNoPromotion(filter, build101);
            assertNotNull(result);
            assertTrue(result.isAccept());
            assertFalse(result.isGoingOn());
        }
    }

    /**
     * Tests the following sequence:
     * <p>
     * <pre>
     *     120
     *     111
     *     110 --> copper
     *     101
     *     100 --> copper
     * </pre>
     * <p>
     * Builds 110 and 100 should be accepted:
     * <ul>
     * <li>From build: 11.*</li>
     * <li>With promotion level: copper</li>
     * </ul>
     */
    @Test
    public void from_build_with_promotion() {
        NamedBuildFilter filter = new NamedBuildFilter(
                NamedBuildFilterData.of("11.*").withWithPromotionLevel("COPPER")
        );
        {
            BuildFilterResult result = filterNoPromotion(filter, build120);
            assertNotNull(result);
            assertFalse(result.isAccept());
            assertTrue(result.isGoingOn());
        }
        {
            BuildFilterResult result = filterNoPromotion(filter, build111);
            assertNotNull(result);
            assertFalse(result.isAccept());
            assertTrue(result.isGoingOn());
        }
        {
            BuildFilterResult result = filterWithPromotions(filter, build110, copper);
            assertNotNull(result);
            assertTrue(result.isAccept());
            assertTrue(result.isGoingOn());
        }
        {
            BuildFilterResult result = filterNoPromotion(filter, build101);
            assertNotNull(result);
            assertFalse(result.isAccept());
            assertTrue(result.isGoingOn());
        }
        {
            BuildFilterResult result = filterWithPromotions(filter, build100, copper);
            assertNotNull(result);
            assertTrue(result.isAccept());
            assertFalse(result.isGoingOn());
        }
    }

    /**
     * Tests the following sequence:
     * <p>
     * <pre>
     *     120
     *     111
     *     110
     *     101
     *     100
     * </pre>
     * <p>
     * Builds 120 and 101 should be accepted:
     * <ul>
     * <li>From build: 11.*</li>
     * <li>To build: 10.*</li>
     * </ul>
     */
    @Test
    public void from_build_to_build() {
        NamedBuildFilter filter = new NamedBuildFilter(
                NamedBuildFilterData.of("12.*").withToBuild("10.*")
        );
        {
            BuildFilterResult result = filterNoPromotion(filter, build120);
            assertNotNull(result);
            assertTrue(result.isAccept());
            assertTrue(result.isGoingOn());
        }
        {
            BuildFilterResult result = filterNoPromotion(filter, build111);
            assertNotNull(result);
            assertFalse(result.isAccept());
            assertTrue(result.isGoingOn());
        }
        {
            BuildFilterResult result = filterNoPromotion(filter, build110);
            assertNotNull(result);
            assertFalse(result.isAccept());
            assertTrue(result.isGoingOn());
        }
        {
            BuildFilterResult result = filterNoPromotion(filter, build101);
            assertNotNull(result);
            assertTrue(result.isAccept());
            assertFalse(result.isGoingOn());
        }
    }

    private BuildFilterResult filterNoPromotion(NamedBuildFilter filter, Build build) {
        return filter.filter(
                Collections.emptyList(),
                branch,
                build,
                () -> new BuildView(
                        build,
                        Collections.emptyList(),
                        Collections.emptyList()
                )
        );
    }

    private BuildFilterResult filterWithPromotions(NamedBuildFilter filter, Build build, PromotionLevel... promotionLevels) {
        return filter.filter(
                Collections.emptyList(),
                branch,
                build,
                () -> new BuildView(
                        build,
                        Arrays.asList(promotionLevels).stream()
                                .map(promotionLevel -> PromotionRun.of(
                                        build,
                                        promotionLevel,
                                        Signature.of("test"),
                                        ""
                                ))
                                .collect(Collectors.toList()),
                        Collections.emptyList()
                )
        );
    }

}