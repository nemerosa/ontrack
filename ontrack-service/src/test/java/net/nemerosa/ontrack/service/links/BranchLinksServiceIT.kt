package net.nemerosa.ontrack.service.links

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.links.BranchLinksService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class BranchLinksServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var branchLinksService: BranchLinksService

    @Test
    fun `Downstream dependencies with multiple source builds`() {
        asAdmin {
            val targetBranch = project().branch()
            val targetBuild1 = targetBranch.build("1")
            val targetBuild2 = targetBranch.build("2")

            project {
                branch {
                    val sourceBuild1 = build("10")
                    val sourceBuild2 = build("11")

                    // sourceBuild1 -> targetBuild1
                    sourceBuild1.linkTo(targetBuild1)
                    // sourceBuild2 -> targetBuild2
                    sourceBuild2.linkTo(targetBuild2)

                    // Get downstream dependencies for the source branch
                    // It should only return the link from the most recent build (sourceBuild2)
                    // to the target build it points to (targetBuild2)
                    val links = branchLinksService.getDownstreamDependencies(this, 10)
                    assertEquals(1, links.size)
                    assertEquals(targetBranch.id, links[0].branch.id)
                    assertEquals(sourceBuild2.id, links[0].sourceBuild.id)
                    assertEquals(targetBuild2.id, links[0].targetBuild.id)
                }
            }
        }
    }

    @Test
    fun `Downstream dependencies with multiple target builds for the same source build`() {
        asAdmin {
            val targetBranch = project().branch()
            val targetBuild1 = targetBranch.build("1")
            val targetBuild2 = targetBranch.build("2")

            project {
                branch {
                    val sourceBuild = build("10")

                    // sourceBuild -> targetBuild1
                    // sourceBuild -> targetBuild2
                    sourceBuild.linkTo(targetBuild1)
                    sourceBuild.linkTo(targetBuild2)

                    // Get downstream dependencies for the source branch
                    // It should return the link to the most recent target build
                    val links = branchLinksService.getDownstreamDependencies(this, 10)
                    assertEquals(1, links.size)
                    assertEquals(targetBranch.id, links[0].branch.id)
                    assertEquals(sourceBuild.id, links[0].sourceBuild.id)
                    assertEquals(targetBuild2.id, links[0].targetBuild.id)
                }
            }
        }
    }

    @Test
    fun `Upstream dependencies with multiple target builds`() {
        asAdmin {
            val sourceBranch = project().branch()
            val sourceBuild1 = sourceBranch.build("1")
            val sourceBuild2 = sourceBranch.build("2")

            project {
                branch {
                    val targetBuild1 = build("10")
                    val targetBuild2 = build("11")

                    // sourceBuild1 -> targetBuild1
                    sourceBuild1.linkTo(targetBuild1)
                    // sourceBuild2 -> targetBuild2
                    sourceBuild2.linkTo(targetBuild2)

                    // Get upstream dependencies for the target branch
                    // In this case targetBranch USES sourceBranch, so sourceBranch is UPSTREAM of targetBranch.
                    val links = branchLinksService.getUpstreamDependencies(this, 10)
                    assertEquals(1, links.size)
                    assertEquals(sourceBranch.id, links[0].branch.id)
                    assertEquals(sourceBuild2.id, links[0].sourceBuild.id)
                    assertEquals(targetBuild2.id, links[0].targetBuild.id)
                }
            }
        }
    }

    @Test
    fun `Downstream dependencies with qualifiers`() {
        asAdmin {
            val targetBranch = project().branch()
            val targetBuild1 = targetBranch.build("1")
            val targetBuild2 = targetBranch.build("2")

            project {
                branch {
                    val sourceBuild = build("10")

                    sourceBuild.linkTo(targetBuild1, "qualifier-1")
                    sourceBuild.linkTo(targetBuild2, "qualifier-2")

                    val links = branchLinksService.getDownstreamDependencies(this, 10)
                    assertEquals(2, links.size)
                    
                    val link1 = links.find { it.qualifier == "qualifier-1" }
                    val link2 = links.find { it.qualifier == "qualifier-2" }

                    assertEquals(targetBuild1.id, link1?.targetBuild?.id)
                    assertEquals(targetBuild2.id, link2?.targetBuild?.id)
                }
            }
        }
    }

    @Test
    fun `Verify fix for cross-project ID comparison in downstream dependencies`() {
        asAdmin {
            // We need a target project to have a build with a LARGER ID than source project's build,
            // but we want to simulate the case where an OLDER target build is already collected.

            val targetBranch = project().branch()
            val targetBuild1 = targetBranch.build("1") // Smaller ID
            val targetBuild2 = targetBranch.build("2") // Larger ID

            project {
                branch {
                    val sourceBuild1 = build("10")
                    val sourceBuild2 = build("11")

                    // sourceBuild2 -> targetBuild1
                    sourceBuild2.linkTo(targetBuild1)
                    
                    // sourceBuild1 -> targetBuild2
                    sourceBuild1.linkTo(targetBuild2)

                    val links = branchLinksService.getDownstreamDependencies(this, 10)
                    assertEquals(1, links.size)

                    // The logic in BranchLinksServiceImpl takes the link with the most recent TARGET build
                    val newestTargetBuild = if (targetBuild1.id.value >= targetBuild2.id.value) targetBuild1 else targetBuild2
                    assertEquals(newestTargetBuild.id.value, links[0].targetBuild.id.value)
                }
            }
        }
    }

    @Test
    fun `Verify fix for cross-project ID comparison - scenario where it could fail before`() {
        asAdmin {
            val targetBranch = project().branch()
            val targetBuild1 = targetBranch.build("1")
            val targetBuild2 = targetBranch.build("2")

            project {
                branch {
                    val sourceBuild1 = build("10")
                    val sourceBuild2 = build("11")

                    // sourceBuild1 -> targetBuild2
                    sourceBuild1.linkTo(targetBuild2)
                    
                    // sourceBuild2 -> targetBuild1
                    sourceBuild2.linkTo(targetBuild1)

                    val links = branchLinksService.getDownstreamDependencies(this, 10)
                    assertEquals(1, links.size)
                    
                    // It should keep the link that points to the most recent TARGET build
                    val newestTargetBuild = if (targetBuild1.id.value >= targetBuild2.id.value) targetBuild1 else targetBuild2
                    assertEquals(newestTargetBuild.id.value, links[0].targetBuild.id.value)
                }
            }
        }
    }
}
