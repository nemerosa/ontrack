package net.nemerosa.ontrack.extension.github.indicators.compliance

import net.nemerosa.ontrack.extension.github.AbstractGitHubTestSupport
import net.nemerosa.ontrack.extension.github.TestOnGitHub
import net.nemerosa.ontrack.extension.github.catalog.GitHubSCMCatalogSettings
import net.nemerosa.ontrack.extension.github.githubTestEnv
import net.nemerosa.ontrack.extension.indicators.computing.*
import net.nemerosa.ontrack.extension.indicators.model.IndicatorService
import net.nemerosa.ontrack.extension.scm.catalog.CatalogLinkService
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalog
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GitHubComplianceIndicatorsIT : AbstractGitHubTestSupport() {

    @Autowired
    private lateinit var complianceIndicators: GitHubComplianceIndicators

    @Autowired
    private lateinit var computingService: IndicatorComputingService

    @Autowired
    private lateinit var indicatorService: IndicatorService

    @Autowired
    private lateinit var configurableIndicatorService: ConfigurableIndicatorService

    @Autowired
    private lateinit var repositoryMustHaveDescription: RepositoryMustHaveDescription

    @Autowired
    private lateinit var repositoryTeamMustHaveDescription: RepositoryTeamMustHaveDescription

    @Autowired
    private lateinit var repositoryDefaultBranchShouldBeMain: RepositoryDefaultBranchShouldBeMain

    @Autowired
    private lateinit var repositoryMustHaveMaintainingTeam: RepositoryMustHaveMaintainingTeam

    @Autowired
    private lateinit var repositoryMustNotHaveAnyAdmin: RepositoryMustNotHaveAnyAdmin

    @Autowired
    private lateinit var repositoryMustHaveAReadme: RepositoryMustHaveAReadme

    // @Autowired
    // TODO private lateinit var repositoryShouldBeInternalOrPrivate: RepositoryShouldBeInternalOrPrivate

    @Autowired
    private lateinit var scmCatalog: SCMCatalog

    @Autowired
    private lateinit var catalogLinkService: CatalogLinkService

    @Test
    fun `Getting the list of configurable compliance indicators`() {
        val indicators = complianceIndicators.configurableIndicators
        // Checks the one about the description is present
        assertNotNull(indicators.find { it.id == "github-compliance-repository-team-must-have-description" }) { indicator ->
            assertEquals("A repository team MUST have the {regex} expression in its description", indicator.name)
            assertEquals(1, indicator.attributes.size)
            val attribute = indicator.attributes.first()
            assertEquals("regex", attribute.key)
            assertEquals("Regular expression", attribute.name)
            assertEquals(ConfigurableIndicatorAttributeType.REGEX, attribute.type)
            assertEquals(true, attribute.required)
        }
    }

    @TestOnGitHub
    fun `Getting all configurable compliance indicators for a given repository`() {
        // Saves the compliance indicators (restoring previous values after the test)
        repositoryDefaultBranchShouldBeMain.save(enabled = true)
        repositoryMustHaveDescription.save(enabled = true)
        repositoryMustHaveMaintainingTeam.save(enabled = true)
        repositoryTeamMustHaveDescription.save(enabled = true)
        repositoryMustNotHaveAnyAdmin.save(enabled = true)
        repositoryMustHaveAReadme.save(enabled = true)
        // TODO repositoryShouldBeInternalOrPrivate.save(enabled = true)
        withSettings(GitHubSCMCatalogSettings::class) {
            try {
                // Project setup
                project {
                    gitHubRealConfig()
                    // Make sure to launch the SCM catalog indexation
                    settingsManagerService.saveSettings(
                        GitHubSCMCatalogSettings(
                            orgs = listOf(githubTestEnv.organization),
                            autoMergeTimeout = GitHubSCMCatalogSettings.DEFAULT_AUTO_MERGE_TIMEOUT,
                            autoMergeInterval = GitHubSCMCatalogSettings.DEFAULT_AUTO_MERGE_INTERVAL,
                        )
                    )
                    scmCatalog.collectSCMCatalog { println(it) }
                    catalogLinkService.computeCatalogLinks()
                    // Saves the list of indicators
                    computingService.compute(complianceIndicators, this)
                    // Gets the list of indicators after they have been saved
                    val indicators = indicatorService.getAllProjectIndicators(this)
                    // Map key x values
                    val values = indicators.associate {
                        it.type.id to it.value
                    }
                    // Checks some values
                    assertEquals(
                        true,
                        values[RepositoryDefaultBranchShouldBeMain.ID],
                        "Test repo default branch should be main"
                    )
                    assertEquals(
                        false,
                        values[RepositoryMustHaveMaintainingTeam.ID],
                        "Test repo must has no maintaining team"
                    )
                    assertEquals(
                        true,
                        values[RepositoryTeamMustHaveDescription.ID],
                        "Test repo must have a description"
                    )
                    assertEquals(true, values[RepositoryMustHaveDescription.ID], "Test repo must have a description")
                    assertEquals(true, values[RepositoryMustNotHaveAnyAdmin.ID], "Test repo has no admin")
                    assertEquals(true, values[RepositoryMustHaveAReadme.ID], "Test repo must have a readme")
                    // TODO assertEquals(false, values[RepositoryShouldBeInternalOrPrivate.ID])
                }
            } finally {
                listOf(
                    repositoryDefaultBranchShouldBeMain,
                    repositoryMustHaveDescription,
                    repositoryMustHaveMaintainingTeam,
                    repositoryTeamMustHaveDescription,
                    repositoryMustNotHaveAnyAdmin,
                    repositoryMustHaveAReadme,
                    // TODO repositoryShouldBeInternalOrPrivate,
                ).forEach { it.delete() }
            }
        }
    }

    private fun GitHubComplianceCheck<*, *>.save(
        enabled: Boolean,
        link: String? = null,
        values: Map<String, String?> = emptyMap(),
    ) {
        toConfigurableIndicatorType().save(enabled, link, values)
    }

    private fun ConfigurableIndicatorType<*, *>.save(
        enabled: Boolean,
        link: String? = null,
        values: Map<String, String?> = emptyMap(),
    ) {
        configurableIndicatorService.saveConfigurableIndicator(
            type = this,
            state = ConfigurableIndicatorState(
                enabled = enabled,
                link = link,
                values = ConfigurableIndicatorState.toAttributeList(this, values),
            )
        )
    }

    private fun GitHubComplianceCheck<*, *>.delete() {
        toConfigurableIndicatorType().delete()
    }

    private fun ConfigurableIndicatorType<*, *>.delete() {
        configurableIndicatorService.saveConfigurableIndicator(
            type = this,
            state = null,
        )
    }

}