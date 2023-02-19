package net.nemerosa.ontrack.extension.git.graphql

import io.mockk.mockk
import net.nemerosa.ontrack.extension.git.AbstractGitTestSupport
import net.nemerosa.ontrack.extension.git.GitChangeLogCache
import net.nemerosa.ontrack.extension.git.model.GitChangeLog
import net.nemerosa.ontrack.json.getRequiredTextField
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import kotlin.test.assertEquals

class GQLRootQueryGitChangeLogByUUIDIT : AbstractGitTestSupport() {

    @Autowired
    private lateinit var gitChangeLogCache: GitChangeLogCache

    @Test
    fun `Git log being cached`() {
        val uuid = UUID.randomUUID().toString()
        asAdmin {
            project {
                val log = GitChangeLog(
                    uuid = uuid,
                    project = this,
                    scmBuildFrom = mockk(),
                    scmBuildTo = mockk(),
                    syncError = false,
                )
                gitChangeLogCache.put(log)
                run(
                    """
                    {
                        gitChangeLogByUUID(uuid: "$uuid") {
                            uuid
                        }
                    }
                """
                ) { data ->
                    assertEquals(uuid, data.path("gitChangeLogByUUID").getRequiredTextField("uuid"))
                }
            }
        }
    }


}