package net.nemerosa.ontrack.extension.git.relnotes

import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import net.nemerosa.ontrack.extension.scm.relnotes.ReleaseNotesRequest
import net.nemerosa.ontrack.extension.scm.relnotes.ReleaseNotesService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

// TODO Missing promotions of builds
// TODO Missing catalog of issues

class GitReleaseNotesIT : AbstractGitTestSupport() {

    @Autowired
    private lateinit var releaseNotesService: ReleaseNotesService

    @Test
    fun `Change log with grouping of releases and issue types`() {
        createRepo {
            sequence(
                    // master branch initial commits
                    1,
                    // first release
                    "release/1.0",
                    2 with "#1 Issue",
                    3 with "#1 Another commit for this issue",
                    4 with "#2 Another issue",
                    // second release
                    "release/1.1",
                    5 with "#3 A fix",
                    6 with "#4 Another fix",
                    // next major release
                    "release/2.0",
                    7 with "#5 Feature",
                    8 with "#5 Feature",
                    // third release, not promoted
                    "release/1.2",
                    9 to "#6 Pending fix",
            )
        } and { repo, commits: Map<Int, String> ->
            // Creates a project for this repo
            val project = project {
                gitProject(repo)
                // master branch
                branch("master") {
                    gitBranch("master") {
                        commitAsProperty()
                    }
                    build("1") {
                        gitCommitProperty(commits.getValue(1))
                    }
                }
                // first release
                branch("release-1.0") {
                    gitBranch("release/1.0") {
                        commitAsProperty()
                    }
                    val pl = promotionLevel("PLATINUM")
                    (2..4).forEach {
                        build(name = it.toString()) {
                            gitCommitProperty(commits.getValue(it))
                            if (it != 2) {
                                promote(pl)
                            }
                        }
                    }
                }
                // second release
                branch("release-1.1") {
                    gitBranch("release/1.1") {
                        commitAsProperty()
                    }
                    val pl = promotionLevel("PLATINUM")
                    (5..6).forEach {
                        build(name = it.toString()) {
                            gitCommitProperty(commits.getValue(it))
                            promote(pl)
                        }
                    }
                }
                // major release
                branch("release-2.0") {
                    gitBranch("release/2.0") {
                        commitAsProperty()
                    }
                    val pl = promotionLevel("PLATINUM")
                    (7..8).forEach {
                        build(name = it.toString()) {
                            gitCommitProperty(commits.getValue(it))
                            if (it == 8) {
                                promote(pl)
                            }
                        }
                    }
                }
                // patch release
                branch("release-1.2") {
                    gitBranch("release/1.2") {
                        commitAsProperty()
                    }
                    (9..9).forEach {
                        build(name = it.toString()) {
                            gitCommitProperty(commits.getValue(it))
                        }
                    }
                }
            }
            // Gets the change log for this project
            val notes = releaseNotesService.exportProjectReleaseNotes(
                    project,
                    ReleaseNotesRequest(
                            branchPattern = "release/.*",
                            branchGrouping = "release/(\\d+).*",
                            branchOrdering = "",
                            buildLimit = 10,
                            promotion = "PLATINUM",
                            format = "text",
                            issueGrouping = "", // TODO
                            issueExclude = "delivery",
                            issueAltGroup = "Misc."
                    )
            )
            assertEquals("text/plain", notes.type)
            assertEquals(
                    """
                        
                    """.trimIndent(),
                    notes.content.toString()
            )
        }
    }

}