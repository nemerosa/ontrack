package net.nemerosa.ontrack.graphql

import net.nemerosa.ontrack.model.structure.Branch
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ValidationRunsPaginationQLIT : AbstractQLKTITSupport() {

    private fun withTestBranch(code: (branch: Branch) -> Unit) {
        project {
            branch {
                val vs = validationStamp("VS")
                (1..100).forEach { buildNo ->
                    build("$buildNo") {
                        validate(vs)
                    }
                }
                code(this)
            }
        }
    }

    @Test
    fun `Getting all validation runs is limited by default to 50`() {
        withTestBranch { branch ->
            run("""{
                branches (id: ${branch.id}) {
                    validationStamps(name: "VS") {
                        validationRuns {
                            build {
                                name
                            }
                        }
                    }
                }
            }""") { data ->
                assertEquals(
                        50,
                        data.path("branches").first()
                                .path("validationStamps").first()
                                .path("validationRuns").size()
                )
            }
        }
    }

    @Test
    fun `Getting 20 first runs`() {
        withTestBranch { branch ->
            run("""{
                branches (id: ${branch.id}) {
                    validationStamps(name: "VS") {
                        validationRuns(count: 20) {
                            build {
                                name
                            }
                        }
                    }
                }
            }""") { data ->
                assertEquals(
                        20,
                        data.path("branches").first()
                                .path("validationStamps").first()
                                .path("validationRuns").size()
                )
            }
        }
    }

}
