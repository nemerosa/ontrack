package net.nemerosa.ontrack.extension.av.graphql

import net.nemerosa.ontrack.extension.av.AbstractAutoVersioningTestSupport
import net.nemerosa.ontrack.extension.av.AutoVersioningTestFixtures
import net.nemerosa.ontrack.extension.av.AutoVersioningTestSupport
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfig
import net.nemerosa.ontrack.extension.queue.QueueNoAsync
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.isNullOrNullNode
import net.nemerosa.ontrack.model.structure.PromotionRun
import net.nemerosa.ontrack.test.TestUtils.uid
import net.nemerosa.ontrack.test.assertJsonNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@QueueNoAsync
class AutoVersioningTrailGQLPromotionRunFieldContributorIT : AbstractAutoVersioningTestSupport() {

    @Autowired
    protected lateinit var autoVersioningTestSupport: AutoVersioningTestSupport

    @Test
    fun `Getting the auto-versioning trail for a promotion run`() {
        withPromotionLevelTargets { pl, app1, app2 ->
            val run = pl.run()
            run(
                """
                        {
                            promotionRuns(id: ${run.id}) {
                                autoVersioningTrail {
                                    branches {
                                        branch {
                                            id
                                        }
                                        configuration {
                                            targetPath
                                        }
                                        rejectionReason
                                    }
                                }
                            }
                        }
                    """, mapOf(

                )
            ) { data ->
                assertEquals(
                    mapOf(
                        "promotionRuns" to listOf(
                            mapOf(
                                "autoVersioningTrail" to mapOf(
                                    "branches" to listOf(
                                        mapOf(
                                            "branch" to mapOf(
                                                "id" to app2.id().toString(),
                                            ),
                                            "configuration" to mapOf(
                                                "targetPath" to "app2.properties"
                                            ),
                                            "rejectionReason" to null,
                                        ),
                                        mapOf(
                                            "branch" to mapOf(
                                                "id" to app1.id().toString(),
                                            ),
                                            "configuration" to mapOf(
                                                "targetPath" to "app1.properties"
                                            ),
                                            "rejectionReason" to null,
                                        ),
                                    )
                                )
                            )
                        )
                    ).asJson(),
                    data
                )
            }
        }
    }

    @Test
    fun `Getting the auto-versioning trail and audit for a promotion run`() {
        withPromotionLevelTargets { pl, app1, app2 ->
            structureService.disableBranch(app2)
            val run = pl.run()
            run(
                """
                        {
                            promotionRuns(id: ${run.id}) {
                                autoVersioningTrail {
                                    branches {
                                        branch {
                                            id
                                        }
                                        rejectionReason
                                        orderId
                                        audit {
                                          order {
                                            uuid
                                          }
                                          mostRecentState {
                                            state
                                          }
                                        }
                                    }
                                }
                            }
                        }
                    """, mapOf(

                )
            ) { data ->
                val branches = data.path("promotionRuns").path(0)
                    .path("autoVersioningTrail").path("branches")
                assertNotNull(branches.find { it.path("rejectionReason").isNull }) { branch ->
                    assertEquals(app1.id(), branch.path("branch").path("id").asInt())
                    val orderId = branch.path("orderId").asText()
                    val audit = branch.path("audit")
                    assertEquals(orderId, audit.path("order").path("uuid").asText())
                }
            }
        }
    }

    @Test
    fun `Getting the auto-versioning trail and audit for a promotion run with one target being disabled`() {
        withPromotionLevelTargets { pl, app1, app2 ->
            structureService.disableBranch(app2)
            val run = pl.run()
            run(
                """
                        {
                            promotionRuns(id: ${run.id}) {
                                autoVersioningTrail {
                                    branches {
                                        branch {
                                            id
                                        }
                                        configuration {
                                            targetPath
                                        }
                                        rejectionReason
                                    }
                                }
                            }
                        }
                    """, mapOf(

                )
            ) { data ->
                assertEquals(
                    mapOf(
                        "promotionRuns" to listOf(
                            mapOf(
                                "autoVersioningTrail" to mapOf(
                                    "branches" to listOf(
                                        mapOf(
                                            "branch" to mapOf(
                                                "id" to app2.id().toString(),
                                            ),
                                            "configuration" to mapOf(
                                                "targetPath" to "app2.properties"
                                            ),
                                            "rejectionReason" to "Branch is disabled",
                                        ),
                                        mapOf(
                                            "branch" to mapOf(
                                                "id" to app1.id().toString(),
                                            ),
                                            "configuration" to mapOf(
                                                "targetPath" to "app1.properties"
                                            ),
                                            "rejectionReason" to null,
                                        ),
                                    )
                                )
                            )
                        )
                    ).asJson(),
                    data
                )
            }
        }
    }

    @Test
    @AsAdminTest
    fun `Getting the paginated trail for a promotion run`() {
        testTrailRun { secondaryTargetProjectName, targetName ->
            mapOf(
                "$secondaryTargetProjectName/main" to null,
                "$targetName/v22" to null,
            )
        }
    }

    @Test
    @AsAdminTest
    fun `Getting the paginated trail for a promotion run with all branches`() {
        testTrailRun(size = 4, onlyEligible = false) { secondaryTargetProjectName, targetName ->
            mapOf(
                "$secondaryTargetProjectName/main" to null,
                "$targetName/v22" to null,
                "$targetName/v21" to "Branch is disabled",
                "$targetName/v20" to "Branch is disabled",
            )
        }
    }

    @Test
    @AsAdminTest
    fun `Getting the paginated trail for a promotion run with filter on project name`() {
        testTrailRun(size = 4, targetProjectName = true) { secondaryTargetProjectName, targetName ->
            mapOf(
                "$targetName/v22" to null,
            )
        }
    }

    private fun testTrailRun(
        size: Int = 2,
        onlyEligible: Boolean = true,
        targetProjectName: Boolean = false,
        expected: (
            secondaryTargetProjectName: String,
            targetName: String,
        ) -> Map<String, String?>,
    ) {
        withMultipleTrailRun { promotionRun, secondaryTargetProjectName, targetName ->
            // Getting the trail using GraphQL
            run(
                $$"""
                query AVTrailQuery($promotionRunId: Int!, $size: Int!, $onlyEligible: Boolean! $projectName: String = null){
                    promotionRuns(id: $promotionRunId) {
                        autoVersioningTrailPaginated(
                            size: $size,
                            filter: {
                                onlyEligible: $onlyEligible,
                                projectName: $projectName,
                            }
                        ) {
                            pageInfo {
                                nextPage {
                                    offset
                                    size
                                }
                            }
                            pageItems {
                                branch {
                                    name
                                    project {
                                        name
                                    }
                                }
                                rejectionReason
                            }
                        }
                    }
                }
            """.trimIndent(),
                mapOf(
                    "promotionRunId" to promotionRun.id(),
                    "size" to size,
                    "onlyEligible" to onlyEligible,
                    "projectName" to targetProjectName.takeIf { it }?.let { targetName },
                )
            ) { data ->
                val page = data.path("promotionRuns").single().path("autoVersioningTrailPaginated")

                val pageInfo = page.path("pageInfo")
                assertJsonNull(pageInfo.path("nextPage"))

                val items = page.path("pageItems")
                val itemData = items.associate {
                    val branchNode = it.path("branch")
                    val branchName = branchNode.path("name").asText()
                    val projectName = branchNode.path("project").path("name").asText()
                    val rejectionReason =
                        it.path("rejectionReason")?.takeIf { reason -> !reason.isNullOrNullNode() }?.asText()
                    "$projectName/$branchName" to rejectionReason
                }

                val expectedData = expected(secondaryTargetProjectName, targetName)
                assertEquals(expectedData, itemData)
            }
        }
    }

    private fun withMultipleTrailRun(
        code: (
            promotionRun: PromotionRun,
            secondaryTargetProjectName: String,
            targetName: String,
        ) -> Unit,
    ) {
        val depName = uid("dep-")
        val depProject = project(depName)
        val depBranch = depProject.branch("main")
        val depPromotionLevel = depBranch.promotionLevel("GOLD")

        val targetName = uid("target-")
        mockSCMTester.withMockSCMRepository {

            val targetProject = project(targetName)
            targetProject.configureMockSCMProject()

            // Several target branches, one of them being disabled
            val versions = listOf("v20", "v21", "v22")
            for (version in versions) {
                val targetBranch = targetProject.branch(version)
                if (version != "v22") {
                    structureService.disableBranch(targetBranch)
                }
                // SCM setup of the target(s)
                targetBranch.configureMockSCMBranch(scmBranch = version)
                repositoryFile(
                    branch = version,
                    path = "versions.properties",
                    content = "version=1.0.0",
                )
                // Setting up the auto-versioning dep --> target
                autoVersioningConfigurationService.setupAutoVersioning(
                    targetBranch,
                    AutoVersioningConfig(
                        configurations = listOf(
                            AutoVersioningTestFixtures.sourceConfig(
                                sourceProject = depName,
                                sourceBranch = depBranch.name,
                                sourcePromotion = depPromotionLevel.name,
                                targetPath = "versions.properties",
                            )
                        )
                    )
                )
            }
        }

        // Secondary target, on a different project
        val secondaryTargetProjectName = uid("secondary-target-")
        val secondaryTargetProject = project(secondaryTargetProjectName)
        mockSCMTester.withMockSCMRepository {
            secondaryTargetProject.configureMockSCMProject()
            val secondaryTargetBranch = secondaryTargetProject.branch("main")
            // SCM setup of the target(s)
            secondaryTargetBranch.configureMockSCMBranch(scmBranch = "main")
            repositoryFile(
                path = "versions.properties",
                branch = "main",
                content = "version=1.0.0",
            )
            // Setting up the auto-versioning dep --> secondary target
            autoVersioningConfigurationService.setupAutoVersioning(
                secondaryTargetBranch,
                AutoVersioningConfig(
                    configurations = listOf(
                        AutoVersioningTestFixtures.sourceConfig(
                            sourceProject = depName,
                            sourceBranch = depBranch.name,
                            sourcePromotion = depPromotionLevel.name,
                            targetPath = "versions.properties",
                        )
                    )
                )
            )
        }

        // Promoting the source
        val depBuild = depBranch.build("2.0.0")
        val promotionRun = depBuild.promote(depPromotionLevel)

        // Waiting for the AV processes to be done for this run
        autoVersioningTestSupport.waitForAutoVersioningToBeDone(promotionRun)

        // OK
        code(
            promotionRun,
            secondaryTargetProjectName,
            targetName
        )
    }
}