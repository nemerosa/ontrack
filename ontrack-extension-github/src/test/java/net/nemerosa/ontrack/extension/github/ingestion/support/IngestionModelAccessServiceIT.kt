package net.nemerosa.ontrack.extension.github.ingestion.support

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.github.ingestion.AbstractIngestionTestSupport
import net.nemerosa.ontrack.extension.github.ingestion.IngestionHookFixtures
import net.nemerosa.ontrack.extension.github.ingestion.processing.*
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Owner
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.normalizeName
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

internal class IngestionModelAccessServiceIT : AbstractIngestionTestSupport() {

    @Autowired
    private lateinit var ingestionModelAccessService: IngestionModelAccessService

    @Test
    fun `Setting a new build run ID`() {
        asAdmin {
            project {
                branch {
                    val payload = IngestionHookFixtures.sampleWorkflowRunPayload(
                        repoName = project.name,
                        runId = 1
                    )
                    val build = build {
                        // Setting the run ID
                        ingestionModelAccessService.setBuildRunId(
                            this,
                            payload.workflowRun
                        )
                    }
                    // Looking for this build using the run ID
                    assertEquals(
                        build,
                        ingestionModelAccessService.findBuildByRunId(payload.repository, 1L)
                    )
                }
            }
        }
    }

    @Test
    fun `Adding a new build run ID`() {
        asAdmin {
            project {
                branch {
                    // Payloads
                    val payload1 = IngestionHookFixtures.sampleWorkflowRunPayload(
                        repoName = project.name,
                        runName = "one",
                        runId = Random.nextLong()
                    )
                    val payload2 = IngestionHookFixtures.sampleWorkflowRunPayload(
                        repoName = project.name,
                        runName = "second",
                        runId = Random.nextLong()
                    )
                    val build = build {
                        // Setting the run ID, once
                        ingestionModelAccessService.setBuildRunId(this, payload1.workflowRun)
                        // Setting the run ID, twice
                        ingestionModelAccessService.setBuildRunId(this, payload2.workflowRun)
                    }
                    // Looking for this build using the first run ID
                    assertEquals(
                        build,
                        ingestionModelAccessService.findBuildByRunId(payload1.repository, payload1.workflowRun.id)
                    )
                    // Looking for this build using the second run ID
                    assertEquals(
                        build,
                        ingestionModelAccessService.findBuildByRunId(payload1.repository, payload2.workflowRun.id)
                    )
                }
            }
        }
    }

    @Test
    fun `Find a project from a repository`() {
        asAdmin {
            project {
                assertNotNull(ingestionModelAccessService.findProjectFromRepository(
                    Repository.stub("any-owner", name)
                )) {
                    assertEquals(this.id, it.id)
                }
            }
        }
    }

    @Test
    fun `Setting up the project with one unique GitHub configuration`() {
        // Only one GitHub configuration
        val config = onlyOneGitHubConfig()
        // Runs the test
        testProject(config)
    }

    @Test
    fun `Setting up the project with one unique GitHub configuration and a non matching configuration name`() {
        // Only one GitHub configuration
        val config = onlyOneGitHubConfig()
        // Runs the test
        assertFailsWith<GitHubConfigProvidedNameNotFoundException> {
            testProject(config, configName = uid("C"))
        }
    }

    @Test
    fun `Setting up the build with one mismatch GitHub configuration`() {
        // Only one GitHub configuration
        val config = onlyOneGitHubConfig()
        // Runs the test
        assertFailsWith<GitHubConfigURLMismatchException> {
            testProject(
                config,
                htmlUrl = "https://github.enterprise.com/nemerosa/github-ingestion-poc",
            )
        }
    }

    @Test
    fun `Setting up the build with no GitHub configuration`() {
        // Only one GitHub configuration
        noGitHubConfig()
        // Runs the test
        assertFailsWith<NoGitHubConfigException> {
            testProject(null)
        }
    }

    @Test
    fun `Setting up the build with several GitHub configurations and none matching`() {
        // Only one GitHub configuration
        severalGitHubConfigs()
        // Runs the test
        assertFailsWith<GitHubConfigURLNoMatchException> {
            testProject(
                null,
                htmlUrl = "https://github.enterprise0.com/nemerosa/github-ingestion-poc"
            )
        }
    }

    @Test
    fun `Setting up the build with several GitHub configurations and an explicit configuration name`() {
        // Only one GitHub configuration
        val config = severalGitHubConfigs()
        // Runs the test
        testProject(
            config,
            configName = config.name,
        )
    }

    @Test
    fun `Setting up the build with several GitHub configurations and one matching`() {
        // Only one GitHub configuration
        val match = severalGitHubConfigs()
        // Runs the test
        testProject(
            match,
            htmlUrl = "${match.url}/nemerosa/github-ingestion-poc",
        )
    }

    @Test
    fun `Setting up the build with several GitHub configurations and several matching`() {
        // Only one GitHub configuration
        val match = severalGitHubConfigs(sameRoot = true)
        // Runs the test
        assertFailsWith<GitHubConfigURLSeveralMatchesException> {
            testProject(
                match,
                htmlUrl = "${match.url}/nemerosa/github-ingestion-poc",
            )
        }
    }

    @Test
    fun `Creating a branch`() {
        asAdmin {
            project {
                val branch = ingestionModelAccessService.getOrCreateBranch(this, "release/1.0", null)
                assertNotNull(
                    structureService.findBranchByName(project.name, "release-1.0").getOrNull(),
                    "Branch created"
                ) {
                    assertEquals(branch.id, it.id, "Same branch")
                }
            }
        }
    }

    @Test
    fun `Creating a branch is forbidden for tags`() {
        project {
            assertFailsWith<IllegalStateException> {
                ingestionModelAccessService.getOrCreateBranch(this, "refs/tags/1.0.0", null)
            }
        }
    }

    @Test
    fun `Keeping a branch`() {
        asAdmin {
            project {
                branch("release-1.0") {
                    val branch = ingestionModelAccessService.getOrCreateBranch(project, "release/1.0", null)
                    assertEquals(this.id, branch.id, "Same branch")
                }
            }
        }
    }

    @Test
    fun `Configuration of the issue service identifier`() {
        val config = onlyOneGitHubConfig()
        testProject(config, issueServiceIdentifier = "jira//config")
    }

    @Test
    fun `Configuration of the indexation interval`() {
        val config = onlyOneGitHubConfig()
        testProject(config, indexationInterval = 60)
    }

    private fun testProject(
        config: GitHubEngineConfiguration?,
        configName: String? = null,
        owner: String = "nemerosa",
        name: String = "github-ingestion-poc",
        htmlUrl: String = "${config?.url ?: "https://github.com"}/$owner/$name",
        issueServiceIdentifier: String = "self",
        indexationInterval: Int = 30,
    ) {
        withGitHubIngestionSettings(
            orgProjectPrefix = false,
            issueServiceIdentifier = issueServiceIdentifier,
            indexationInterval = indexationInterval,
        ) {
            asAdmin {
                ingestionModelAccessService.getOrCreateProject(
                    Repository(
                        name = name,
                        description = null,
                        owner = Owner(login = owner),
                        htmlUrl = htmlUrl,
                    ),
                    configuration = configName,
                )
                val projectName = normalizeName(name)
                assertNotNull(structureService.findProjectByName(projectName).getOrNull()) { project ->
                    assertNotNull(
                        getProperty(project, GitHubProjectConfigurationPropertyType::class.java),
                        "GitHub config set on project"
                    ) {
                        assertEquals(config?.name, it.configuration.name)
                        assertEquals("$owner/$name", it.repository)
                        assertEquals(indexationInterval, it.indexationInterval)
                        assertEquals(issueServiceIdentifier, it.issueServiceConfigurationIdentifier)
                    }
                }
            }
        }
    }

    @Test
    fun `Setup validation stamp when non existing`() {
        project {
            branch {
                asAdmin {
                    val setup = ingestionModelAccessService.setupValidationStamp(this, "VS", "Validation stamp name")
                    assertEquals("VS", setup.name)
                    assertEquals("Validation stamp name", setup.description)
                }
            }
        }
    }

    @Test
    fun `Setup validation stamp when existing with null input description`() {
        project {
            branch {
                val vs = validationStamp("VS", description = "Validation stamp name")
                asAdmin {
                    val setup = ingestionModelAccessService.setupValidationStamp(this, "VS", null)
                    assertEquals(vs.id, setup.id)
                    assertEquals("VS", setup.name)
                    assertEquals("Validation stamp name", setup.description)
                }
            }
        }
    }

    @Test
    fun `Setup validation stamp when existing with new input description`() {
        project {
            branch {
                val vs = validationStamp("VS", description = "Validation stamp name")
                asAdmin {
                    val setup = ingestionModelAccessService.setupValidationStamp(this, "VS", "Another description")
                    assertEquals(vs.id, setup.id)
                    assertEquals("VS", setup.name)
                    assertEquals("Another description", setup.description)
                    assertEquals(setup.description, structureService.getValidationStamp(vs.id).description)
                }
            }
        }
    }

    @Test
    fun `Setup promotion level when non existing`() {
        project {
            branch {
                asAdmin {
                    val setup = ingestionModelAccessService.setupPromotionLevel(this, "PL", "Promotion level name")
                    assertEquals("PL", setup.name)
                    assertEquals("Promotion level name", setup.description)
                }
            }
        }
    }

    @Test
    fun `Setup promotion level when existing with null input description`() {
        project {
            branch {
                val pl = promotionLevel("PL", "Promotion level name")
                asAdmin {
                    val setup = ingestionModelAccessService.setupPromotionLevel(this, "PL", null)
                    assertEquals(pl.id, setup.id)
                    assertEquals("PL", setup.name)
                    assertEquals("Promotion level name", setup.description)
                }
            }
        }
    }

    @Test
    fun `Setup promotion level when existing with new input description`() {
        project {
            branch {
                val pl = promotionLevel("PL", "Promotion level name")
                asAdmin {
                    val setup = ingestionModelAccessService.setupPromotionLevel(this, "PL", "Another description")
                    assertEquals(pl.id, setup.id)
                    assertEquals("PL", setup.name)
                    assertEquals("Another description", setup.description)
                    assertEquals(setup.description, structureService.getPromotionLevel(pl.id).description)
                }
            }
        }
    }

}