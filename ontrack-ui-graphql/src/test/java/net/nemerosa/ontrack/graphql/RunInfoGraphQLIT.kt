package net.nemerosa.ontrack.graphql

import net.nemerosa.ontrack.model.structure.RunInfoInput
import net.nemerosa.ontrack.model.structure.RunInfoService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RunInfoGraphQLIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var runInfoService: RunInfoService

    @Test
    fun `Getting the run info for a build`() {
        val build = doCreateBuild()
        // Adds some run info
        asAdmin().execute {
            runInfoService.setRunInfo(
                    build,
                    RunInfoInput(runTime = 30)
            )
        }
        // GraphQL query
        val data = run("""
            {
                builds(id: ${build.id}) {
                    runInfo {
                        id
                        runTime
                    }
                }
            }
        """)
        // Checks
        val runInfo = data["builds"].first()["runInfo"]
        assertTrue(runInfo["id"].asInt() > 0)
        assertEquals(30, runInfo["runTime"].asInt())
    }

    @Test
    fun `Getting the run info for a build without one`() {
        val build = doCreateBuild()
        // GraphQL query
        val data = run("""
            {
                builds(id: ${build.id}) {
                    runInfo {
                        id
                        runTime
                    }
                }
            }
        """)
        // Checks
        val runInfo = data["builds"].first()["runInfo"]
        assertTrue(runInfo.isNull, "No run info")
    }

}