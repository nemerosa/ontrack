package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.browser.pages.GitHubConfigurationPage
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import net.nemerosa.ontrack.test.TestUtils
import org.junit.Test

import static net.nemerosa.ontrack.acceptance.steps.BasicSteps.loginAsAdmin

/**
 * GUI tests about the `github` extension.
 */
@AcceptanceTestSuite
class ACCGitHubExtension extends AcceptanceTestClient {

    @Test
    void 'Creation of a configuration'() {
        browser { browser ->
            // Random name
            String configurationName = TestUtils.uid('C')
            // Logs in
            def homePage = loginAsAdmin(browser)
            // Screenshot after login
            browser.screenshot 'github-after-login'
            // Goes to the GitHub configuration page
            def configurationPage = homePage.selectUserMenu(GitHubConfigurationPage, 'github-configurations-link')
            // Screenshot after login
            browser.screenshot 'github-configuration-page-before'
            // Creates a configuration
            configurationPage.createConfiguration {
                browser.screenshot 'github-configuration-dialog'
                name = configurationName
            }
            // Checks the configuration is displayed
            browser.screenshot 'github-configuration-page-after'
            def configuration = configurationPage.getConfiguration(configurationName)
            assert configuration != null
            assert configuration.name == configurationName
            assert configuration.url == 'https://github.com'
        }
    }

}
