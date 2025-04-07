package net.nemerosa.ontrack.boot.graphql

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.getRequiredJsonField
import net.nemerosa.ontrack.json.getRequiredTextField
import net.nemerosa.ontrack.json.getTextField
import net.nemerosa.ontrack.model.security.BranchFilterMgt
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.security.ProjectView
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.fail

class GraphQLLinksIT : AbstractQLKTITSupport() {

    @Test
    fun `Build filters links`() {
        project {
            branch {
                promotionLevel("IRON")
                asUser()
                    .withProjectFunction(this, ProjectView::class.java)
                    .withProjectFunction(this, BranchFilterMgt::class.java)
                    .call {
                        buildFilterService.saveFilter(
                            id,
                            true,
                            "My filter",
                            "net.nemerosa.ontrack.service.StandardBuildFilterProvider",
                            mapOf(
                                "withPromotionLevel" to "IRON"
                            ).asJson()
                        )
                        run(
                            """
                                {
                                    branches(id: $id) {
                                        buildFilterResources {
                                            name
                                            links {
                                                _update
                                            }
                                        }
                                    }
                                }
                            """
                        ) { data ->
                            val branch = data.path("branches").path(0)
                            assertNotNull(branch.getRequiredJsonField("buildFilterResources").find {
                                it.getRequiredTextField("name") == "My filter"
                            }) { filter ->
                                val links = filter.getRequiredJsonField("links")
                                assertNotNull(links.getTextField("_update"), "Update link is present")
                            }
                        }
                    }
            }
        }
    }

    @Test
    fun `Validation stamp image link`() {
        val vs = doCreateValidationStamp()
        val data = run(
            """{
            branches (id: ${vs.branch.id}) {
                validationStamps {
                    links {
                        _image
                    }
                }
            }
        }"""
        )
        assertEquals(
            "urn:test:net.nemerosa.ontrack.boot.ui.ValidationStampController#getValidationStampImage_:,${vs.id}",
            data["branches"][0]["validationStamps"][0]["links"]["_image"].asText()
        )
    }

    @Test
    fun `Promotion level image link`() {
        val pl = doCreatePromotionLevel()
        val data = run(
            """{
            branches (id: ${pl.branch.id}) {
                promotionLevels {
                    links {
                        _image
                    }
                }
            }
        }"""
        )
        assertEquals(
            "urn:test:net.nemerosa.ontrack.boot.ui.PromotionLevelController#getPromotionLevelImage_:,${pl.id}",
            data["branches"][0]["promotionLevels"][0]["links"]["_image"].asText()
        )
    }

    @Test
    fun `Branch links`() {
        val branch = doCreateBranch()

        val data = run("""{branches (id: ${branch.id}) { name links { _page } } }""")
        assertEquals(branch.name, data["branches"][0]["name"].asText())
        assertEquals("urn:test:#:entity:BRANCH:${branch.id}", data["branches"][0]["links"]["_page"].asText())
    }

    @Test
    fun `Project links`() {
        val p = doCreateProject()
        val data = asUser().with(p, ProjectEdit::class.java).call {
            run("{projects(id: ${p.id}) { name links { _update } }}")
        }
        assertEquals(p.name, data["projects"][0]["name"].asText())
        assertEquals(
            "urn:test:net.nemerosa.ontrack.boot.ui.ProjectController#saveProject:${p.id},",
            data["projects"][0]["links"]["_update"].asText()
        )
    }

    @Test
    fun `Account token links`() {
        asUser {
            val id = securityService.currentUser?.account?.id()
                ?: fail("No current user")
            asAdmin {
                val data = run(
                    """{
                        accounts(id: $id) {
                            links {
                                _revokeToken
                                _generateToken
                                _token
                            }
                        }
                    }"""
                )
                val links = data["accounts"][0]["links"]
                assertFalse(links["_revokeToken"].isNull)
                assertFalse(links["_generateToken"].isNull)
                assertFalse(links["_token"].isNull)
            }
        }
    }
}
