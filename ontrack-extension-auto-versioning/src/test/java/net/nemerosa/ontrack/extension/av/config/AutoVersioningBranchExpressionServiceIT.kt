package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class AutoVersioningBranchExpressionServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var autoVersioningBranchExpressionService: AutoVersioningBranchExpressionService

    @Test
    fun `Using &regex`() {
        project {

            val source11 = branch(name = "release-1.11")
            val source12 = branch(name = "release-1.12")
            /* val source20 = */ branch(name = "release-1.20")

            project {
                branch {
                    val target = this

                    val latest =
                        autoVersioningBranchExpressionService.getLatestBranch(
                            eligibleTargetBranch = target,
                            promotion = "ANY",
                            project = source11.project,
                            avBranchExpression = "regex:release-1\\.1.*",
                        )
                    assertEquals(source12, latest)
                }
            }
        }
    }

    @Test
    fun `Using &same`() {
        project {
            branch {
                val source = this

                project {
                    branch(name = source.name) {
                        val target = this

                        val latest =
                            autoVersioningBranchExpressionService.getLatestBranch(
                                eligibleTargetBranch = target,
                                promotion = "ANY",
                                project = source.project,
                                avBranchExpression = "same"
                            )
                        assertEquals(source, latest)
                    }
                }
            }
        }
    }

    @Test
    fun `Using &same-release`() {
        project {
            val source = this
            /* val source2410 = */ branch("release/1.24.10")
            val source2411 = branch("release/1.24.11")
            val source2500 = branch("release/1.25.0")

            project {
                branch(name = "release/1.24.6") {
                    val target = this

                    assertEquals(
                        source2411,
                        autoVersioningBranchExpressionService.getLatestBranch(
                            eligibleTargetBranch = target,
                            promotion = "ANY",
                            project = source,
                            avBranchExpression = "same-release:2"
                        )
                    )

                    assertEquals(
                        source2500,
                        autoVersioningBranchExpressionService.getLatestBranch(
                            eligibleTargetBranch = target,
                            promotion = "ANY",
                            project = source,
                            avBranchExpression = "same-release:1"
                        )
                    )

                    assertEquals(
                        source2500,
                        autoVersioningBranchExpressionService.getLatestBranch(
                            eligibleTargetBranch = target,
                            promotion = "ANY",
                            project = source,
                            avBranchExpression = "same-release"
                        )
                    )
                }
            }
        }
    }

}