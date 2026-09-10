package net.nemerosa.ontrack.demo.seed

import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DemoSeedTest {

    private val clock = Clock.fixed(Instant.parse("2026-09-01T10:15:30Z"), ZoneOffset.UTC)

    /**
     * In `git log` order — the newest commit first — because that is what
     * [GitChangelogSource] yields, and the seed has to turn that into a branch which reads
     * the right way round.
     */
    private val changelog = listOf(
        ChangelogEntry("e4f5a6b", "#1680 Report the full version in the running application", LocalDateTime.of(2026, 8, 31, 9, 30)),
        ChangelogEntry("a1b2c3d", "#1664 Drive the demo deployment through the CLI", LocalDateTime.of(2026, 8, 30, 14, 0)),
    )

    private fun seed(target: DemoTarget) = DemoSeed(target, clock, log = {})

    @Test
    fun `running it twice in a row yields the same demo state`() {
        val target = InMemoryDemoTarget()
        val dataset = DemoContent.dataset(changelog)

        seed(target).run(dataset)
        val first = target.snapshot()

        seed(target).run(dataset)
        val second = target.snapshot()

        assertEquals(first, second)
    }

    @Test
    fun `the reset deletes whatever was there before`() {
        val target = InMemoryDemoTarget()
        target.createProject("left-over", "A project a visitor created.")
        target.createEnvironment("left-over-env", 500, "An environment nobody remembers.")

        seed(target).run(DemoContent.dataset(changelog))

        val snapshot = target.snapshot()
        assertTrue("left-over" !in snapshot, "The left-over project is gone")
        assertTrue("left-over-env" !in snapshot, "The left-over environment is gone")
    }

    @Test
    fun `the curated dataset and the changelog project are both created`() {
        val target = InMemoryDemoTarget()

        seed(target).run(DemoContent.dataset(changelog))

        assertEquals(
            listOf(
                DemoContent.LIBRARY,
                DemoContent.SERVICE,
                DemoContent.UI,
                DemoContent.CHANGELOG,
            ),
            target.projects().map { it.name },
        )
        assertEquals(
            listOf(DemoContent.STAGING, DemoContent.PRODUCTION),
            target.environments().map { it.name },
        )
    }

    @Test
    fun `one build per changelog entry, named after the commit`() {
        val target = InMemoryDemoTarget()

        seed(target).run(DemoContent.dataset(changelog))

        val snapshot = target.snapshot()
        changelog.forEach { entry ->
            assertTrue(
                "build ${entry.id} \"${entry.message}\" at ${entry.time}" in snapshot,
                "Build for commit ${entry.id}",
            )
        }
    }

    /**
     * Yontrack orders the builds of a branch by creation ORDER, newest first — the build
     * created last is the one every view shows first, whatever creation time it carries.
     * `git log` yields the newest commit first, so seeding the entries as they come makes
     * the branch read backwards: the pipeline timeline showed the oldest commit leftmost
     * and the builds table showed it on top (#1647).
     */
    @Test
    fun `the changelog builds are created oldest commit first, so the branch reads newest first`() {
        val target = InMemoryDemoTarget()

        seed(target).run(DemoContent.dataset(changelog))

        val snapshot = target.snapshot()
        val newest = snapshot.indexOf("build ${changelog.first().id} ")
        val oldest = snapshot.indexOf("build ${changelog.last().id} ")
        assertTrue(oldest >= 0 && newest >= 0, "Both commits have a build")
        assertTrue(
            oldest < newest,
            "The oldest commit is created first, so Yontrack shows the newest one first",
        )
    }

    @Test
    fun `an empty changelog still leaves the changelog project standing`() {
        val target = InMemoryDemoTarget()

        seed(target).run(DemoContent.dataset(emptyList()))

        assertTrue(target.projects().any { it.name == DemoContent.CHANGELOG })
    }

    @Test
    fun `build creation times follow the clock, not the wall clock`() {
        val target = InMemoryDemoTarget()

        seed(target).run(DemoContent.dataset(changelog))

        // The most recent curated build is one day old, at 09:00.
        assertTrue(
            "at 2026-08-31T09:00" in target.snapshot(),
            "A build dated one day before the fixed clock",
        )
    }

    /**
     * A build promoted in two hours' time reads as a defect in Yontrack rather than in the dataset,
     * and it is what a naive hour-per-rung produces for the newest build of the demo: `DaysAgo(0)`
     * is a time of DAY, so every reset running before it - and before the rungs stacked on top of
     * it - dated the whole ladder in the future.
     */
    @Test
    fun `nothing of the demo is dated after the reset which created it`() {
        val target = InMemoryDemoTarget()
        // Early enough in the day that the curated dataset's own hour is still ahead of it, which
        // is the case this exists to catch
        val now = LocalDateTime.of(2026, 9, 1, 6, 30)
        val early = Clock.fixed(now.toInstant(ZoneOffset.UTC), ZoneOffset.UTC)
        DemoSeed(target, early, log = {}).run(DemoContent.dataset(changelog))

        target.projects().forEach { project ->
            (project as InMemoryDemoTarget.InMemoryProject).branches.forEach { branch ->
                branch.builds.forEach { build ->
                    assertTrue(
                        !build.creation.isAfter(now),
                        "Build ${build.name} of ${project.name}/${branch.name} is created in the future",
                    )
                    build.promotions.forEach { (promotion, time) ->
                        assertTrue(
                            !time.isAfter(now),
                            "Promotion $promotion of build ${build.name} is dated in the future",
                        )
                        assertTrue(
                            !time.isBefore(build.creation),
                            "Promotion $promotion of build ${build.name} is dated before the build",
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `the demo dashboard is reset like everything else`() {
        val target = InMemoryDemoTarget()
        target.saveDashboard(DemoDashboard("some-uuid", "A visitor's dashboard", emptyList()))

        seed(target).run(DemoContent.dataset(changelog))

        assertEquals(listOf("Yontrack demo"), target.dashboards().map { it.name })
    }

    /**
     * The seed deletes before it creates, so a dataset the server would reject must be
     * caught before anything is gone. `release/1.3` — an illegal Yontrack entity name — is
     * the case that motivated this: it was found half-way through a real reset.
     */
    @Test
    fun `an invalid dataset is refused before anything is deleted`() {
        val target = InMemoryDemoTarget()
        seed(target).run(DemoContent.dataset(changelog))
        val before = target.snapshot()

        val error = assertFailsWith<IllegalArgumentException> {
            seed(target).run(
                DemoDataset(
                    projects = listOf(
                        ProjectSpec(
                            name = "one",
                            description = "",
                            branches = listOf(BranchSpec(name = "release/1.3", description = "")),
                        ),
                    ),
                )
            )
        }

        assertTrue("release/1.3" in error.message.orEmpty())
        assertEquals(before, target.snapshot(), "The demo is untouched")
    }

    @Test
    fun `a dataset pointing at a build it never creates is refused`() {
        val error = assertFailsWith<IllegalArgumentException> {
            seed(InMemoryDemoTarget()).run(
                DemoDataset(
                    projects = listOf(
                        ProjectSpec(
                            name = "one",
                            description = "",
                            branches = listOf(
                                BranchSpec(
                                    name = "main",
                                    description = "",
                                    builds = listOf(
                                        BuildSpec(
                                            name = "1",
                                            description = "",
                                            creation = BuildCreation.DaysAgo(1),
                                            links = listOf(BuildRef("other", "main", "1")),
                                        ),
                                    ),
                                ),
                            ),
                        ),
                    ),
                )
            )
        }
        assertTrue("never creates" in error.message.orEmpty())
    }

    @Test
    fun `a build promoted to a level its branch does not declare is refused`() {
        val error = assertFailsWith<IllegalArgumentException> {
            seed(InMemoryDemoTarget()).run(
                DemoDataset(
                    projects = listOf(
                        ProjectSpec(
                            name = "one",
                            description = "",
                            branches = listOf(
                                BranchSpec(
                                    name = "main",
                                    description = "",
                                    builds = listOf(
                                        BuildSpec(
                                            name = "1",
                                            description = "",
                                            creation = BuildCreation.DaysAgo(1),
                                            promotionLevels = listOf("BRONZE"),
                                        ),
                                    ),
                                ),
                            ),
                        ),
                    ),
                )
            )
        }
        assertTrue("BRONZE" in error.message.orEmpty())
    }

    /**
     * The delivery map's slot half has nothing to show without admission rules: they are what
     * joins a slot to a promotion level and to another slot. This pins the demo's own story so
     * that an edit which quietly drops it fails here rather than on the demo.
     */
    @Test
    fun `the demo slots carry the admission rules the delivery map reads`() {
        val target = InMemoryDemoTarget()
        seed(target).run(DemoContent.dataset(changelog))
        val snapshot = target.snapshot()

        assertTrue("rule silver promotion" in snapshot, "Staging admits SILVER builds")
        assertTrue("rule gold promotion" in snapshot, "Production admits GOLD builds")
        assertTrue("rule staging environment" in snapshot, "Production requires staging")
        assertTrue("rule mainOnly branchPattern" in snapshot, "Production takes main only")
    }

    /**
     * The promotion half of the delivery map has nothing to draw without these two properties: the
     * dependencies give it its *requires* edges between promotion levels, and auto promotion gives
     * it its *unlocks* edges and every validation stamp checkpoint it has. This pins the demo's own
     * story so that an edit which quietly drops it fails here rather than on the demo.
     */
    @Test
    fun `the demo carries the promotion properties the delivery map reads`() {
        val target = InMemoryDemoTarget()
        seed(target).run(DemoContent.dataset(changelog))

        // Read off the fake server rather than out of the dataset: what is under test is that the
        // seed APPLIES them, in a pass late enough for every name they use to resolve.
        val branch = (target.projects().single { it.name == DemoContent.SERVICE } as InMemoryDemoTarget.InMemoryProject)
            .branches.single { it.name == DemoContent.MAIN }

        assertEquals(listOf(DemoContent.SILVER), branch.promotionDependencies[DemoContent.GOLD])
        assertEquals(
            AutoPromotionSpec(
                validationStamps = listOf(DemoContent.BUILD),
                promotionLevels = listOf(DemoContent.BRONZE),
                include = DemoContent.TESTS_PATTERN,
            ),
            branch.autoPromotions[DemoContent.SILVER],
            "SILVER is granted by BRONZE, by BUILD, and by the stamps matching the pattern",
        )
    }

    /**
     * The pattern is the only thing that produces an AGGREGATE checkpoint on the map, and it is the
     * easiest half of the auto promotion to lose in an edit - the demo would still draw edges, just
     * not that one. Pinned against the stamp names it has to select, and against the one it must
     * not: a stamp outside every edge is what shows that the map draws only what takes part in one.
     */
    @Test
    fun `the demo pattern selects the test stamps and leaves the security scan out`() {
        val autoPromotion = DemoContent.dataset(changelog).projects
            .single { it.name == DemoContent.SERVICE }
            .branches.first()
            .promotionLevels.single { it.name == DemoContent.SILVER }
            .autoPromotion

        assertNotNull(autoPromotion)
        assertEquals(
            listOf(DemoContent.UNIT_TESTS, DemoContent.INTEGRATION_TESTS),
            listOf(DemoContent.UNIT_TESTS, DemoContent.INTEGRATION_TESTS, DemoContent.SECURITY_SCAN)
                .filter { autoPromotionSelectsStamp(it, autoPromotion) },
        )
    }

    /**
     * *Arriving is not the same as succeeding* is the delivery map's central subtlety, and only a
     * validation stamp can show it: a promotion level names a build which WAS promoted, a slot one
     * which WAS deployed, and only a stamp names a build which got there and failed.
     *
     * The demo has to carry one, or the documentation explains a reading nothing on screen shows.
     * A checkpoint's build is the LATEST one to have run that stamp, which is what makes this
     * fragile in an edit: adding a greener build after the failing one takes the reading away
     * without touching anything that looks related.
     */
    @Test
    fun `the demo shows a validation stamp whose latest build arrived and failed`() {
        val failing = DemoContent.dataset(changelog).projects.flatMap { project ->
            project.branches.flatMap { branch ->
                // On the map at all: a stamp is drawn only when some auto promotion of the branch
                // names it or matches it
                val onTheMap = branch.promotionLevels
                    .mapNotNull { it.autoPromotion }
                    .flatMap { auto ->
                        branch.validationStamps.map { it.name }.filter { autoPromotionSelectsStamp(it, auto) }
                    }
                    .toSet()
                onTheMap.mapNotNull { stamp ->
                    // Builds are declared oldest first, so the last one to have run the stamp is
                    // the build its checkpoint names
                    val latest = branch.builds.lastOrNull { build ->
                        build.validations.any { it.validationStamp == stamp }
                    }
                    val run = latest?.validations?.last { it.validationStamp == stamp }
                    if (run != null && !validationStatusPasses(run.status)) {
                        "${project.name}/${branch.name} ${latest.name} $stamp"
                    } else {
                        null
                    }
                }
            }
        }

        assertTrue(
            failing.isNotEmpty(),
            "At least one validation stamp of the demo is drawn showing a build which arrived and failed",
        )
    }

    /**
     * Auto promotion must reproduce the promotions the dataset declares rather than add any: a
     * promotion nobody wrote down would be stamped with the time of the reset instead of the
     * build's own, and the counts the pipeline view shows would stop matching the dataset.
     *
     * The fake server does not fire auto promotions, so this checks the dataset's own consistency -
     * that every build satisfying the rule is already declared as promoted by it.
     */
    @Test
    fun `no build of the demo would be auto promoted beyond what the dataset declares`() {
        DemoContent.dataset(changelog).projects.forEach { project ->
            project.branches.forEach { branch ->
                val passed = { build: BuildSpec, stamp: String ->
                    build.validations.any { it.validationStamp == stamp && validationStatusPasses(it.status) }
                }
                branch.promotionLevels.forEach { promotionLevel ->
                    val autoPromotion = promotionLevel.autoPromotion ?: return@forEach
                    branch.builds.forEach { build ->
                        val satisfied =
                            branch.validationStamps
                                .map { it.name }
                                .filter { autoPromotionSelectsStamp(it, autoPromotion) }
                                .all { passed(build, it) } &&
                                    autoPromotion.promotionLevels.all { it in build.promotionLevels }
                        if (satisfied) {
                            assertTrue(
                                promotionLevel.name in build.promotionLevels,
                                "Build ${build.name} of ${project.name}/${branch.name} satisfies the " +
                                        "auto promotion of ${promotionLevel.name} and must declare it",
                            )
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `a promotion depending on a level the branch does not declare is caught before anything is deleted`() {
        val error = assertFailsWith<IllegalArgumentException> {
            seed(InMemoryDemoTarget()).run(
                datasetWithPromotionLevels(PromotionLevelSpec("GOLD", "", dependsOn = listOf("SILVER")))
            )
        }
        assertTrue("requires SILVER" in error.message.orEmpty(), error.message.orEmpty())
    }

    @Test
    fun `a promotion depending on itself is caught before anything is deleted`() {
        val error = assertFailsWith<IllegalArgumentException> {
            seed(InMemoryDemoTarget()).run(
                datasetWithPromotionLevels(PromotionLevelSpec("GOLD", "", dependsOn = listOf("GOLD")))
            )
        }
        assertTrue("requires itself" in error.message.orEmpty(), error.message.orEmpty())
    }

    @Test
    fun `an auto promotion naming a stamp the branch does not declare is caught before anything is deleted`() {
        val error = assertFailsWith<IllegalArgumentException> {
            seed(InMemoryDemoTarget()).run(
                datasetWithPromotionLevels(
                    PromotionLevelSpec(
                        "GOLD", "",
                        autoPromotion = AutoPromotionSpec(validationStamps = listOf("SMOKE")),
                    )
                )
            )
        }
        assertTrue("auto promoted by SMOKE" in error.message.orEmpty(), error.message.orEmpty())
    }

    /**
     * A pattern selecting nothing draws no aggregate checkpoint AND grants the promotion the moment
     * anything else it names is satisfied: it reads as configuration and behaves as none. The
     * product accepts it - it is what #1705 is about - but curated content must not carry one.
     */
    @Test
    fun `an auto promotion pattern matching no stamp of the branch is caught before anything is deleted`() {
        val error = assertFailsWith<IllegalArgumentException> {
            seed(InMemoryDemoTarget()).run(
                datasetWithPromotionLevels(
                    PromotionLevelSpec("GOLD", "", autoPromotion = AutoPromotionSpec(include = ".*SMOKE")),
                    validationStamps = listOf(ValidationStampSpec("BUILD", "")),
                )
            )
        }
        assertTrue("selects none of the branch" in error.message.orEmpty(), error.message.orEmpty())
    }

    /**
     * The server refuses the promotion as it is created, so a build promoted to GOLD before the
     * SILVER it depends on is refused even though it ends up carrying both. Caught from the dataset
     * rather than discovered half-way through a reset that has already deleted the demo.
     */
    @Test
    fun `a build promoted before the promotion it depends on is caught before anything is deleted`() {
        val error = assertFailsWith<IllegalArgumentException> {
            seed(InMemoryDemoTarget()).run(
                datasetWithPromotionLevels(
                    PromotionLevelSpec("SILVER", ""),
                    PromotionLevelSpec("GOLD", "", dependsOn = listOf("SILVER")),
                    builds = listOf(
                        BuildSpec(
                            name = "1",
                            description = "",
                            creation = BuildCreation.DaysAgo(1),
                            promotionLevels = listOf("GOLD", "SILVER"),
                        ),
                    ),
                )
            )
        }
        assertTrue("promoted to GOLD before SILVER" in error.message.orEmpty(), error.message.orEmpty())
    }

    private fun datasetWithPromotionLevels(
        vararg promotionLevels: PromotionLevelSpec,
        validationStamps: List<ValidationStampSpec> = emptyList(),
        builds: List<BuildSpec> = emptyList(),
    ) = DemoDataset(
        projects = listOf(
            ProjectSpec(
                name = "one",
                description = "",
                branches = listOf(
                    BranchSpec(
                        name = "main",
                        description = "",
                        promotionLevels = promotionLevels.toList(),
                        validationStamps = validationStamps,
                        builds = builds,
                    ),
                ),
            ),
        ),
    )

    /**
     * The demo's one deliberately broken configuration, which the delivery map draws as two
     * unresolved checkpoints (#1705). Both halves of it are easy to "fix" by accident - giving
     * the UI a GOLD promotion level, or a staging slot - and either would quietly take away the
     * thing the map exists to show.
     */
    @Test
    fun `the demo keeps the UI production slot pointing at things which do not exist`() {
        val dataset = DemoContent.dataset(changelog)

        val ui = dataset.projects.single { it.name == DemoContent.UI }
        assertTrue(
            ui.branches.none { branch -> branch.promotionLevels.any { it.name == DemoContent.GOLD } },
            "The UI declares no GOLD promotion level, so its production slot asks for one in vain",
        )
        assertTrue(
            dataset.environments.single { it.name == DemoContent.STAGING }
                .slots.none { it.project == DemoContent.UI },
            "The UI has no staging slot, so its production slot requires a deployment which cannot happen",
        )

        val slot = dataset.environments.single { it.name == DemoContent.PRODUCTION }
            .slots.single { it.project == DemoContent.UI }
        assertEquals(
            listOf(DemoContent.GOLD, DemoContent.STAGING),
            slot.admissionRules.map { it.config["promotion"] ?: it.config["environmentName"] },
            "The two rules still name what they cannot find",
        )
    }

    /**
     * The one thing the demo's slot story hangs on, and the one an edit is most likely to
     * break: production holds a build of `main`, staging a build of the maintenance branch, so
     * the delivery map of `main` has a slot naming another branch's build to draw.
     */
    @Test
    fun `the demo leaves staging holding a maintenance build and production a main one`() {
        val target = InMemoryDemoTarget()
        seed(target).run(DemoContent.dataset(changelog))

        val slots = target.environments()
            .flatMap { (it as InMemoryDemoTarget.InMemoryEnvironment).slots }
            .filter { it.project.name == DemoContent.SERVICE }
            .associateBy { it.environment.name }

        assertEquals("89", slots.getValue(DemoContent.STAGING).deployments.last().name)
        assertEquals(
            DemoContent.MAINTENANCE,
            slots.getValue(DemoContent.STAGING).deployments.last().branch.name,
        )
        assertEquals("104", slots.getValue(DemoContent.PRODUCTION).deployments.last().name)
        assertEquals(
            DemoContent.MAIN,
            slots.getValue(DemoContent.PRODUCTION).deployments.last().branch.name,
        )
    }

    @Test
    fun `a deployment of a build the slot would refuse is caught before anything is deleted`() {
        // Not a theoretical case: the promotion and environment rules of the demo's own slots
        // make the ORDER of its deployments load-bearing
        val error = assertFailsWith<IllegalArgumentException> {
            seed(InMemoryDemoTarget()).run(
                DemoDataset(
                    projects = listOf(
                        ProjectSpec(
                            name = "one",
                            description = "",
                            branches = listOf(
                                BranchSpec(
                                    name = "main",
                                    description = "",
                                    promotionLevels = listOf(PromotionLevelSpec("GOLD", "")),
                                    builds = listOf(
                                        BuildSpec(name = "1", description = "", creation = BuildCreation.DaysAgo(1)),
                                    ),
                                ),
                            ),
                        ),
                    ),
                    environments = listOf(
                        EnvironmentSpec(
                            name = "production",
                            order = 100,
                            description = "",
                            slots = listOf(
                                SlotSpec(
                                    project = "one",
                                    description = "",
                                    admissionRules = listOf(
                                        SlotAdmissionRuleSpec("gold", "promotion", mapOf("promotion" to "GOLD")),
                                    ),
                                ),
                            ),
                        ),
                    ),
                    deployments = listOf(
                        DeploymentSpec("production", BuildRef("one", "main", "1")),
                    ),
                )
            )
        }
        assertTrue("promoted to GOLD" in error.message.orEmpty(), error.message.orEmpty())
    }

    /**
     * The same rule, enforced on the whole dataset rather than only on the changelog: a
     * branch whose builds are declared newest first reads backwards in every view.
     */
    @Test
    fun `a branch whose builds are declared newest first is refused`() {
        val error = assertFailsWith<IllegalArgumentException> {
            seed(InMemoryDemoTarget()).run(
                DemoDataset(
                    projects = listOf(
                        ProjectSpec(
                            name = "one",
                            description = "",
                            branches = listOf(
                                BranchSpec(
                                    name = "main",
                                    description = "",
                                    builds = listOf(
                                        BuildSpec(name = "2", description = "", creation = BuildCreation.DaysAgo(1)),
                                        BuildSpec(name = "1", description = "", creation = BuildCreation.DaysAgo(5)),
                                    ),
                                ),
                            ),
                        ),
                    ),
                )
            )
        }
        assertTrue("oldest first" in error.message.orEmpty(), error.message.orEmpty())
    }

    @Test
    fun `every problem is reported at once, not one reset at a time`() {
        val error = assertFailsWith<IllegalArgumentException> {
            seed(InMemoryDemoTarget()).run(
                DemoDataset(
                    projects = listOf(
                        ProjectSpec(
                            name = "not a name",
                            description = "",
                            branches = listOf(BranchSpec(name = "release/1.3", description = "")),
                        ),
                    ),
                )
            )
        }
        assertTrue("not a name" in error.message.orEmpty())
        assertTrue("release/1.3" in error.message.orEmpty())
    }
}
