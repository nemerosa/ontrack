package net.nemerosa.ontrack.extension.scm.catalog.api

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.scm.catalog.CatalogLinkService
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalog
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogAccessFunction
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.model.security.Roles
import net.nemerosa.ontrack.model.structure.NameDescription.nd
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CatalogGraphQLIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var scmCatalog: SCMCatalog

    @Autowired
    private lateinit var catalogLinkService: CatalogLinkService

    @Test
    fun `Collection of entries and linking`() {
        // Collection of entries
        scmCatalog.collectSCMCatalog { println(it) }
        // Checks entry has been collected
        val collectionData = withGrantViewToAll {
            asUserWith<SCMCatalogAccessFunction, JsonNode> {
                run("""{
                    scmCatalog {
                        pageItems {
                            scm
                            config
                            repository
                            repositoryPage
                        }
                    }
                }""")
            }
        }
        val item = collectionData["scmCatalog"]["pageItems"][0]
        assertEquals("mocking", item["scm"].asText())
        assertEquals("MainConfig", item["config"].asText())
        assertEquals("project/repository", item["repository"].asText())
        assertEquals("uri:project/repository", item["repositoryPage"].asText())
        // Search on orphan entries
        val orphanData = withGrantViewToAll {
            asUserWith<SCMCatalogAccessFunction, JsonNode> {
                run("""{
                    scmCatalog(link: "ORPHAN") {
                        pageItems {
                            repository
                            project {
                                name
                            }
                        }
                    }
                }""")
            }
        }
        assertNotNull(orphanData) {
            val orphanItem = it["scmCatalog"]["pageItems"][0]
            assertEquals("project/repository", orphanItem["repository"].asText())
            assertTrue(orphanItem["project"].isNull, "Entry is not linked")
        }
        // Link with one project
        val name = uid("repository-")
        project(nd(name, "")) {
            // Collection of catalog links
            catalogLinkService.computeCatalogLinks()
            // Checks the link has been recorded
            val linkedEntry = catalogLinkService.getSCMCatalogEntry(this)
            assertNotNull(linkedEntry, "Project is linked to a SCM catalog entry") {
                assertEquals("mocking", it.scm)
                assertEquals("MainConfig", it.config)
                assertEquals("project/repository", it.repository)
                assertEquals("uri:project/repository", it.repositoryPage)
            }
            // Getting the SCM entry through GraphQL & project root
            val data = withGrantViewToAll {
                asUserWith<SCMCatalogAccessFunction, JsonNode> {
                    run("""
                        query ProjectInfo {
                            projects(id: $id) {
                                scmCatalogEntry {
                                    scm
                                    config
                                    repository
                                    repositoryPage
                                }
                            }
                        }
                    """)
                }
            }
            // Checking data
            assertNotNull(data) {
                val entry = it["projects"][0]["scmCatalogEntry"]
                assertEquals("mocking", entry["scm"].asText())
                assertEquals("MainConfig", entry["config"].asText())
                assertEquals("project/repository", entry["repository"].asText())
                assertEquals("uri:project/repository", entry["repositoryPage"].asText())
            }
            // Getting the data through GraphQL & catalog entries
            val entryCollectionData = withGrantViewToAll {
                asUserWith<SCMCatalogAccessFunction, JsonNode> {
                    run("""{
                        scmCatalog {
                            pageItems {
                                project {
                                    name
                                }
                            }
                        }
                    }""")
                }
            }
            assertNotNull(entryCollectionData) {
                val project = it["scmCatalog"]["pageItems"][0]["project"]
                assertEquals(name, project["name"].asText())
            }
        }
        // Search on linked entries
        val data = withGrantViewToAll {
            asUserWith<SCMCatalogAccessFunction, JsonNode> {
                run("""{
                    scmCatalog(link: "LINKED") {
                        pageItems {
                            project {
                                name
                            }
                        }
                    }
                }""")
            }
        }
        assertNotNull(data) {
            val project = it["scmCatalog"]["pageItems"][0]["project"]
            assertEquals(name, project["name"].asText())
        }
    }

    @Test
    fun `SCM Catalog accessible to administrators in view-to-all mode`() {
        scmCatalogTest {
            withGrantViewToAll {
                asAdmin(it)
            }
        }
    }

    @Test
    fun `SCM Catalog accessible to administrators in restricted mode`() {
        scmCatalogTest {
            withNoGrantViewToAll {
                asAdmin(it)
            }
        }
    }

    @Test
    fun `SCM Catalog accessible to global read only`() {
        scmCatalogTest {
            withNoGrantViewToAll {
                asAccountWithGlobalRole(Roles.GLOBAL_READ_ONLY, it)
            }
        }
    }

    private fun scmCatalogTest(setup: (code: () -> Unit) -> Unit) {
        // Collection of entries
        scmCatalog.collectSCMCatalog { println(it) }
        // Link with one project
        val name = uid("repository-")
        project(nd(name, "")) {
            // Collection of catalog links
            catalogLinkService.computeCatalogLinks()
        }
        // Checks rights
        setup {
            val data = run("""{
                    scmCatalog(link: "LINKED") {
                        pageItems {
                            project {
                                name
                            }
                        }
                    }
                }""")
            assertNotNull(data) {
                val entry = it["scmCatalog"]["pageItems"][0]
                assertEquals(name, entry["project"]["name"].asText())
            }
        }
    }

}