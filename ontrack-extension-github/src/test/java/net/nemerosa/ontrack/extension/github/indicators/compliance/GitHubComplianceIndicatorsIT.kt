package net.nemerosa.ontrack.extension.github.indicators.compliance

import net.nemerosa.ontrack.extension.github.AbstractGitHubTestSupport
import net.nemerosa.ontrack.extension.github.catalog.GitHubSCMCatalogSettings
import net.nemerosa.ontrack.extension.github.githubTestEnv
import net.nemerosa.ontrack.extension.indicators.computing.*
import net.nemerosa.ontrack.extension.indicators.model.IndicatorService
import net.nemerosa.ontrack.extension.scm.catalog.CatalogLinkService
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalog
import org.junit.Test
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

    @Test
    fun `Getting all configurable compliance indicators for a given repository`() {
        // Saves the compliance indicators (restoring previous values after the test)
        repositoryDefaultBranchShouldBeMain.save(enabled = true)
        repositoryMustHaveDescription.save(enabled = true)
        repositoryMustHaveMaintainingTeam.save(enabled = true)
        repositoryTeamMustHaveDescription.save(enabled = true)
        withSettings(GitHubSCMCatalogSettings::class) {
            try {
                // Project setup
                project {
                    gitHubRealConfig()
                    // Make sure to launch the SCM catalog indexation
                    settingsManagerService.saveSettings(GitHubSCMCatalogSettings(orgs = listOf(githubTestEnv.organization)))
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
                    // Map key x compliance
                    val compliances = indicators.associate {
                        it.type.id to it.compliance?.value
                    }
                    // Checks some values
                    assertEquals(false, values[RepositoryDefaultBranchShouldBeMain.ID])
                    assertEquals(false, values[RepositoryMustHaveMaintainingTeam.ID])
                    assertEquals(false, values[RepositoryTeamMustHaveDescription.ID])
                    assertEquals(true, values[RepositoryMustHaveDescription.ID])
                    // Checks some compliance
                    assertEquals(50, compliances[RepositoryDefaultBranchShouldBeMain.ID])
                    assertEquals(0, compliances[RepositoryMustHaveMaintainingTeam.ID])
                    assertEquals(0, compliances[RepositoryTeamMustHaveDescription.ID])
                    assertEquals(100, compliances[RepositoryMustHaveDescription.ID])
                }
            } finally {
                listOf(
                    repositoryDefaultBranchShouldBeMain,
                    repositoryMustHaveDescription,
                    repositoryMustHaveMaintainingTeam,
                    repositoryTeamMustHaveDescription,
                ).forEach { it.delete() }
            }
        }
    }

    private fun GitHubComplianceCheck<*, *>.save(
        enabled: Boolean,
        link: String? = null,
        values: Map<String, String?> = emptyMap()
    ) {
        toConfigurableIndicatorType().save(enabled, link, values)
    }

    private fun ConfigurableIndicatorType<*, *>.save(
        enabled: Boolean,
        link: String? = null,
        values: Map<String, String?> = emptyMap()
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