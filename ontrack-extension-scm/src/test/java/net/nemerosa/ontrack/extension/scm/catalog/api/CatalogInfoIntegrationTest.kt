package net.nemerosa.ontrack.extension.scm.catalog.api

import net.nemerosa.ontrack.extension.scm.catalog.CatalogInfoCollector
import net.nemerosa.ontrack.extension.scm.catalog.CatalogLinkService
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalog
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.model.structure.NameDescription.nd
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Integration tests
 */
class CatalogInfoIntegrationTest : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var scmCatalog: SCMCatalog

    @Autowired
    private lateinit var catalogLinkService: CatalogLinkService

    @Autowired
    private lateinit var catalogInfoCollector: CatalogInfoCollector

    @Test
    fun `Collection of entries, linking and collection of infos`() {
        // Collection of entries
        scmCatalog.collectSCMCatalog { println(it) }
        // Checks entry has been collected
        val collectionData = run("""{
            scmCatalog {
                pageItems {
                    scm
                    config
                    repository
                    repositoryPage
                }
            }
        }""")
        val item = collectionData["scmCatalog"]["pageItems"][0]
        assertEquals("mocking", item["scm"].asText())
        assertEquals("MainConfig", item["config"].asText())
        assertEquals("project/repository", item["repository"].asText())
        assertEquals("uri:project/repository", item["repositoryPage"].asText())
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
            // Fires the collection of information for this project
            catalogInfoCollector.collectCatalogInfo(this) { println(it) }
            // Getting the data through GraphQL & project root
            val data = run("""
                query ProjectInfo {
                    projects(id: $id) {
                        scmCatalogEntryLink {
                            scmCatalogEntry {
                                scm
                                config
                                repository
                                repositoryPage
                            }
                            infos {
                                id
                                name
                                data
                                feature {
                                    id
                                }
                            }
                        }
                    }
                }
            """)
            // Checking data
            assertNotNull(data) {
                val link = it["projects"][0]["scmCatalogEntryLink"]

                val entry = link["scmCatalogEntry"]
                assertEquals("mocking", entry["scm"].asText())
                assertEquals("MainConfig", entry["config"].asText())
                assertEquals("project/repository", entry["repository"].asText())
                assertEquals("uri:project/repository", entry["repositoryPage"].asText())

                val infos = link["infos"]
                assertEquals(1, infos.size())
                val info = infos[0]
                assertEquals("net.nemerosa.ontrack.extension.scm.catalog.mock.MockCatalogInfoContributor", info["id"].asText())
                assertEquals("mock", info["name"].asText())
                assertEquals("$name@project/repository", info["data"]["value"].asText())
                assertEquals("test", info["feature"]["id"].asText())
            }
            // Getting the data through GraphQL & catalog entries
            val entryCollectionData = run("""{
                scmCatalog {
                    pageItems {
                        link {
                            project {
                                name
                            }
                            infos {
                                id
                                name
                                data
                                feature {
                                    id
                                }
                            }
                        }
                    }
                }
            }""")
            assertNotNull(entryCollectionData) {
                val link = it["scmCatalog"]["pageItems"][0]["link"]
                assertEquals(name, link["project"]["name"].asText())

                val infos = link["infos"]
                assertEquals(1, infos.size())
                val info = infos[0]
                assertEquals("net.nemerosa.ontrack.extension.scm.catalog.mock.MockCatalogInfoContributor", info["id"].asText())
                assertEquals("mock", info["name"].asText())
                assertEquals("$name@project/repository", info["data"]["value"].asText())
                assertEquals("test", info["feature"]["id"].asText())
            }
        }
    }

}