package net.nemerosa.ontrack.extension.git.relnotes

import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import net.nemerosa.ontrack.extension.scm.relnotes.ReleaseNotesRequest
import net.nemerosa.ontrack.extension.scm.relnotes.ReleaseNotesService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class GitReleaseNotesIT : AbstractGitTestSupport() {

    @Autowired
    private lateinit var releaseNotesService: ReleaseNotesService

    @Test
    fun `Change log with no grouping of releases and no issue types and no exclusion`() {
        mainScenario(
                request = ReleaseNotesRequest(
                        branchPattern = "release/.*",
                        branchGrouping = "",
                        branchGroupFormat = "Release %s",
                        branchOrdering = "",
                        branchOrderingParameter = null,
                        buildLimit = 10,
                        promotion = "PLATINUM",
                        format = "text",
                        issueGrouping = "",
                        issueExclude = "",
                        issueAltGroup = "Misc."
                ),
                expectedReleaseNotes = """
                    ## 2.0.2
                    
                    * #5 Issue #5
                    * #7 Issue #7

                    ## 1.1.1
                    
                    * #4 Issue #4

                    ## 1.1.0

                    * #3 Issue #3

                    ## 1.0.2
                    
                    * #2 Issue #2
                    """.trimIndent()
        )
    }

    @Test
    fun `Change log with one build`() {
        mainScenario(
                request = ReleaseNotesRequest(
                        branchPattern = "release/.*",
                        branchGrouping = "",
                        branchGroupFormat = "Release %s",
                        branchOrdering = "",
                        branchOrderingParameter = null,
                        buildLimit = 1,
                        promotion = "PLATINUM",
                        format = "text",
                        issueGrouping = "",
                        issueExclude = "",
                        issueAltGroup = "Misc."
                ),
                expectedReleaseNotes = """
                    ## 2.0.2
                    
                    * #5 Issue #5
                    * #7 Issue #7
                    """.trimIndent()
        )
    }

    @Test
    fun `Change log with grouping of releases and issue types`() {
        mainScenario(
                request = ReleaseNotesRequest(
                        branchPattern = "release/.*",
                        branchGrouping = "release/(\\d+).*",
                        branchGroupFormat = "Release %s",
                        branchOrdering = "",
                        branchOrderingParameter = null,
                        buildLimit = 10,
                        promotion = "PLATINUM",
                        format = "text",
                        issueGrouping = "Fixes=bug|Features=feature|Enhancements=enhancement",
                        issueExclude = "delivery",
                        issueAltGroup = "Misc."
                ),
                expectedReleaseNotes = """
                    Release 2
                    =========
                    
                    ## 2.0.2

                    Features
                    
                    * #5 Issue #5
                    
                    Release 1
                    =========

                    ## 1.1.1

                    Enhancements
                    
                    * #4 Issue #4

                    ## 1.1.0

                    Fixes

                    * #3 Issue #3

                    ## 1.0.2

                    Fixes
                    
                    * #2 Issue #2
                    """.trimIndent()
        )
    }

    private fun mainScenario(
            request: ReleaseNotesRequest,
            expectedReleaseNotes: String
    ) {
        createRepo {
            sequenceWithPauses(
                    // master branch initial commits
                    1,
                    // first release
                    "release/1.0",
                    2 with "#1 Issue", // 1.0.0 Platinum
                    3 with "#1 Another commit for this issue", // 1.0.1
                    4 with "#2 Another issue", // 1.0.2 Platinum
                    // second release
                    "release/1.1",
                    5 with "#3 A fix", // 1.1.0 Platinum
                    6 with "#4 Another fix", // 1.1.1 Platinum
                    // next major release
                    "release/2.0",
                    7 with "#5 Feature",
                    8 with "#5 Feature again",
                    9 with "#7 Feature", // 1.2.0 Platinum
                    // third release, not promoted
                    "release/1.2" from "release/1.1",
                    10 with "#6 Pending fix" // 1.2.0
            )
        } and { repo, commits: Map<Int, String> ->
            // Registering the issues
            mockIssue(id = 1, type = "bug")
            mockIssue(id = 2, type = "bug")
            mockIssue(id = 3, type = "bug")
            mockIssue(id = 4, type = "enhancement")
            mockIssue(id = 5, type = "feature")
            mockIssue(id = 6, type = "bug")
            mockIssue(id = 7, type = "delivery")
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
                        build(name = "1.0.${it - 2}") {
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
                        build(name = "1.1.${it - 5}") {
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
                    (7..9).forEach {
                        build(name = "2.0.${it - 7}") {
                            gitCommitProperty(commits.getValue(it))
                            if (it == 9) {
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
                    (10..10).forEach {
                        build(name = "1.2.${it - 10}") {
                            gitCommitProperty(commits.getValue(it))
                        }
                    }
                }
            }
            // Gets the change log for this project
            val notes = releaseNotesService.exportProjectReleaseNotes(
                    project,
                    request
            )
            assertEquals("text/plain", notes.type)
            val actualNotes = notes.content.toString(Charsets.UTF_8).trimIndent()
            assertEquals(
                    expectedReleaseNotes.trim(),
                    actualNotes.trim()
            )
        }
    }

}