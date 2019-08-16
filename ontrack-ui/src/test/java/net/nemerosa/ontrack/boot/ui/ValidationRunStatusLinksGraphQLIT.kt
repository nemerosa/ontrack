package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import org.junit.Test
import kotlin.test.assertEquals

class ValidationRunStatusLinksGraphQLIT : AbstractQLKTITSupport() {

    @Test
    fun `Comment edition link for authorized user`() {
        project {
            branch {
                val vs = validationStamp()
                build {
                    validate(vs, ValidationRunStatusID.STATUS_FAILED) {
                        val status = validationStatus(
                                ValidationRunStatusID.STATUS_INVESTIGATING,
                                "My comment"
                        ).lastStatus
                        // Query
                        val data = run("""{
                            validationRuns(id: $id) {
                                validationRunStatuses {
                                    id
                                    links {
                                       _comment
                                    }
                                }
                            }
                        }""")
                        val json = data["validationRuns"][0]["validationRunStatuses"][0]
                        assertEquals(
                                status.id(),
                                json["id"].intValue()
                        )
                        assertEquals(
                                "urn:test:net.nemerosa.ontrack.boot.ui.ValidationRunStatusController#getValidationRunStatusEditCommentForm:${status.id}",
                                json["links"]["_comment"].textValue()
                        )
                    }
                }
            }
        }
    }

}