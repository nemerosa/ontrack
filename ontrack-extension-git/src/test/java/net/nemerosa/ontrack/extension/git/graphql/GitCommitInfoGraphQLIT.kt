package net.nemerosa.ontrack.extension.git.graphql

import net.nemerosa.ontrack.extension.git.AbstractGitSearchTestSupport
import net.nemerosa.ontrack.extension.git.GitCommitSearchExtension
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.toJson
import net.nemerosa.ontrack.test.TestUtils
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class GitCommitInfoGraphQLIT : AbstractGitSearchTestSupport() {

    @Autowired
    protected lateinit var gitCommitSearchExtension: GitCommitSearchExtension

    @Test
    fun `Getting commit info`() {
        createRepo {
            sequenceWithPauses(
                    (1..10)
            )
        } and { repo, commits: Map<Int, String> ->
            project {
                gitProject(repo)
                // Setup
                branch("master") {
                    gitBranch("master") {
                        commitAsProperty()
                    }
                    // Creates some builds on this branch, for some commits only
                    build(1, commits)
                    build(3, commits)
                    build(5, commits)
                    build(9, commits)
                }
                // Re-indexes the commits
                searchIndexService.index(gitCommitSearchExtension)
                // Getting commit info
                val data = asUserWithView {
                    run("""
                        query CommitInfo(${'$'}commit: String!) {
                            gitCommitInfo(commit: ${'$'}commit) {
                                uiCommit {
                                    annotatedMessage
                                }
                                branchInfosList {
                                    type
                                    branchInfoList {
                                        branch {
                                            name
                                        }
                                        firstBuild {
                                            name
                                        }
                                    }
                                }
                            }
                        }
                    """, mapOf(
                            "commit" to commits.getValue(2)
                    ))
                }
                val gitCommitInfo = data["gitCommitInfo"]

                assertEquals(
                        mapOf(
                                "uiCommit" to mapOf(
                                        "annotatedMessage" to "Commit 2"
                                ),
                                "branchInfosList" to listOf(
                                        mapOf(
                                                "type" to "Development",
                                                "branchInfoList" to listOf(
                                                        mapOf(
                                                                "branch" to mapOf(
                                                                        "name" to "master"
                                                                ),
                                                                "firstBuild" to mapOf(
                                                                        "name" to "3"
                                                                )
                                                        )
                                                )
                                        )
                                )
                        ).asJson(),
                        gitCommitInfo
                )
            }
        }
    }

    @Test
    fun `Getting commit info for a project`() {
        createRepo {
            sequenceWithPauses(
                    (1..3),
                    "release/2.0",
                    4,
                    "master",
                    5,
                    "release/2.0",
                    (6..7),
                    "master",
                    (8..10)
            )
        } and { repo, commits: Map<Int, String> ->
            project {
                gitProject(repo)
                branch("master") {
                    gitBranch("master") {
                        commitAsProperty()
                    }
                    // Validations
                    val test1 = validationStamp("Test1")
                    val test2 = validationStamp("Test2")
                    // Promotions
                    val silver = promotionLevel("SILVER")
                    // Creates some builds on this branch
                    build(1, commits, listOf(test1))
                    build(3, commits, listOf(test1, test2), listOf(silver))
                    build(5, commits)
                    build(9, commits, listOf(test1, test2), listOf(silver))
                }
                branch("release-2.0") {
                    gitBranch("release/2.0") {
                        commitAsProperty()
                    }
                    // Validations
                    val test1 = validationStamp("Test1")
                    val test2 = validationStamp("Test2")
                    // Promotions
                    val silver = promotionLevel("SILVER")
                    val gold = promotionLevel("GOLD")
                    // Creates some builds on this branch
                    build(4, commits, listOf(test1))
                    build(8, commits, listOf(test1, test2), listOf(silver, gold))
                }
                // Getting commit info
                val data = run("""
                    query CommitInfo(${'$'}project: String!, ${'$'}commit: String!) {
                        projects(name: ${'$'}project) {
                            gitCommitInfo(commit: ${'$'}commit) {
                                uiCommit {
                                    annotatedMessage
                                }
                                branchInfosList {
                                    type
                                    branchInfoList {
                                        branch {
                                            name
                                        }
                                        firstBuild {
                                            name
                                        }
                                        promotions {
                                            promotionLevel {
                                                name
                                            }
                                            build {
                                                name
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                """, mapOf(
                        "project" to name,
                        "commit" to commits.getValue(2)
                ))
                val project = data["projects"][0]
                val gitCommitInfo = project["gitCommitInfo"]

                TestUtils.assertJsonEquals(
                        mapOf(
                                "uiCommit" to mapOf(
                                        "annotatedMessage" to "Commit 2"
                                ),
                                "branchInfosList" to listOf(
                                        mapOf(
                                                "type" to "Development",
                                                "branchInfoList" to listOf(
                                                        mapOf(
                                                                "branch" to mapOf(
                                                                        "name" to "master"
                                                                ),
                                                                "firstBuild" to mapOf(
                                                                        "name" to "3"
                                                                ),
                                                                "promotions" to listOf(
                                                                        mapOf(
                                                                                "promotionLevel" to mapOf(
                                                                                        "name" to "SILVER"
                                                                                ),
                                                                                "build" to mapOf(
                                                                                        "name" to "3"
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        ),
                                        mapOf(
                                                "type" to "Releases",
                                                "branchInfoList" to listOf(
                                                        mapOf(
                                                                "branch" to mapOf(
                                                                        "name" to "release-2.0"
                                                                ),
                                                                "firstBuild" to mapOf(
                                                                        "name" to "4"
                                                                ),
                                                                "promotions" to listOf(
                                                                        mapOf(
                                                                                "promotionLevel" to mapOf(
                                                                                        "name" to "SILVER"
                                                                                ),
                                                                                "build" to mapOf(
                                                                                        "name" to "8"
                                                                                )
                                                                        ),
                                                                        mapOf(
                                                                                "promotionLevel" to mapOf(
                                                                                        "name" to "GOLD"
                                                                                ),
                                                                                "build" to mapOf(
                                                                                        "name" to "8"
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        ).toJson(),
                        gitCommitInfo
                )
            }
        }
    }

}