package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.extension.av.AbstractAutoVersioningTestSupport
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class MostRecentBranchSourceIT : AbstractAutoVersioningTestSupport() {

    @Autowired
    private lateinit var mostRecentBranchSource: MostRecentBranchSource

    @Test
    fun `No promotion at all`() {
        val promotionName = uid("pl-")
        project {
            val dep = this
            branch("release-1.1") {
                promotionLevel(promotionName)
            }
            val dep12 = branch("release-1.2") {
                promotionLevel(promotionName)
            }

            val parent = project()
            val parentBranch = parent.branch("main")

            val latestBranch = mostRecentBranchSource.getLatestBranch(
                config = "release-1\\..*",
                project = dep,
                targetBranch = parentBranch,
                promotion = promotionName,
            )

            assertEquals(dep12, latestBranch)

        }
    }

    @Test
    fun `Promotion on the latest branch only`() {
        val promotionName = uid("pl-")
        project {
            val dep = this
            branch("release-1.1") {
                promotionLevel(promotionName)
            }
            val dep12 = branch("release-1.2") {
                val pl = promotionLevel(promotionName)
                build {
                    promote(pl)
                }
            }

            val parent = project()
            val parentBranch = parent.branch("main")

            val latestBranch = mostRecentBranchSource.getLatestBranch(
                config = "release-1\\..*",
                project = dep,
                targetBranch = parentBranch,
                promotion = promotionName,
            )

            assertEquals(dep12, latestBranch)

        }
    }

    @Test
    fun `Promotion on the latest branch and a previous branch`() {
        val promotionName = uid("pl-")
        project {
            val dep = this
            branch("release-1.1") {
                val pl = promotionLevel(promotionName)
                build {
                    promote(pl)
                }
            }
            val dep12 = branch("release-1.2") {
                val pl = promotionLevel(promotionName)
                build {
                    promote(pl)
                }
            }

            val parent = project()
            val parentBranch = parent.branch("main")

            val latestBranch = mostRecentBranchSource.getLatestBranch(
                config = "release-1\\..*",
                project = dep,
                targetBranch = parentBranch,
                promotion = promotionName,
            )

            assertEquals(dep12, latestBranch)

        }
    }

    @Test
    fun `Promotion on a previous branch only`() {
        val promotionName = uid("pl-")
        project {
            val dep = this
            val dep11 = branch("release-1.1") {
                val pl = promotionLevel(promotionName)
                build {
                    promote(pl)
                }
            }
            branch("release-1.2") {
                promotionLevel(promotionName)
            }

            val parent = project()
            val parentBranch = parent.branch("main")

            val latestBranch = mostRecentBranchSource.getLatestBranch(
                config = "release-1\\..*",
                project = dep,
                targetBranch = parentBranch,
                promotion = promotionName,
            )

            assertEquals(dep11, latestBranch)

        }
    }
}
