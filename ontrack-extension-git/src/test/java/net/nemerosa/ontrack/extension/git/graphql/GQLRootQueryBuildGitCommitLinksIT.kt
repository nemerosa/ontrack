package net.nemerosa.ontrack.extension.git.graphql

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GQLRootQueryBuildGitCommitLinksIT : AbstractQLKTITSupport() {

    @Test
    fun `Getting list of types of build Git commit links`() {
        run(
            """{
                    buildGitCommitLinks {
                        id
                        name
                    }
                }"""
        ) { data ->
            val list = data.path("buildGitCommitLinks")
            val gitCommitLink = list.find { it.path("id").asText() == "git-commit-property" }
            assertNotNull(gitCommitLink) {
                assertEquals("Git Commit Property", it.path("name").asText())
            }
        }
    }

}