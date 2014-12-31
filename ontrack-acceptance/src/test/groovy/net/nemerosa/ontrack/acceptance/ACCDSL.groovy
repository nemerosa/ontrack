package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.support.AcceptanceTest
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import net.nemerosa.ontrack.client.ClientNotFoundException
import net.nemerosa.ontrack.dsl.Branch
import net.nemerosa.ontrack.dsl.Ontrack
import net.nemerosa.ontrack.dsl.OntrackConnection
import net.nemerosa.ontrack.dsl.Shell
import org.junit.Before
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * Ontrack DSL tests.
 */
@AcceptanceTestSuite
@AcceptanceTest(excludes = 'production')
class ACCDSL extends AcceptanceTestClient {

    private Ontrack ontrack

    @Before
    void init() {
        ontrack = OntrackConnection.create(baseURL)
                .disableSsl(sslDisabled)
                .authenticate('admin', adminPassword)
                .build()
    }

    @Test(expected = ClientNotFoundException)
    void 'Branch not authorised'() {
        // Creating a branch
        def testBranch = doCreateBranch()
        // Anonymous client
        ontrack = OntrackConnection.create(baseURL).disableSsl(sslDisabled).build()
        // Branch cannot be found
        ontrack.branch(testBranch.project.name.asText(), testBranch.name.asText())
    }

    @Test
    void 'Getting last promoted build'() {
        def branch = createBuildsAndPromotions()
        // Getting the last promoted builds
        def results = branch.lastPromotedBuilds
        assert results.collect { it.name } == ['3', '2', '1']
    }

    @Test
    void 'Filtering build on promotion'() {
        Branch branch = createBuildsAndPromotions()
        // Filtering builds on promotion
        def results = branch.standardFilter withPromotionLevel: 'BRONZE'
        assert results.collect { it.name } == ['2']
    }

    @Test
    void 'Validating some builds and filtering on it'() {
        def branch = createBuildsAndPromotions()
        // Validating builds
        ontrack.build(branch.project, branch.name, '1').validate('SMOKE', 'FAILED')
        ontrack.build(branch.project, branch.name, '2').validate('SMOKE', 'PASSED')
        // Filtering on validation
        assert branch.standardFilter(withValidationStamp: 'SMOKE').collect { it.name } == ['2', '1']
        // Filtering on validation status
        assert branch.standardFilter(withValidationStamp: 'SMOKE', withValidationStampStatus: 'PASSED').collect {
            it.name
        } == ['2']
        // Filtering since validation status
        assert branch.standardFilter(sinceValidationStamp: 'SMOKE', sinceValidationStampStatus: 'PASSED').collect {
            it.name
        } == ['3', '2']
    }

    @Test
    void 'Definition of a project and a branch'() {
        def project = uid('P')
        ontrack.project(project) {
            branch('1.0') {
                promotionLevel 'COPPER', 'Copper promotion'
                validationStamp 'SMOKE', 'Smoke tests'
            }
        }
        // Checks the branch does exist
        assert ontrack.branch(project, '1.0').name == '1.0'
        // Checks the structure
        assert ontrack.promotionLevel(project, '1.0', 'COPPER').name == 'COPPER'
        assert ontrack.validationStamp(project, '1.0', 'SMOKE').name == 'SMOKE'
    }

    @Test
    void 'Setting the Jenkins build property on a build'() {
        // Jenkins configuration
        ontrack.configure {
            jenkins 'Local', 'http://jenkins'
        }
        // Creating a branch
        def project = uid('P')
        ontrack.project(project).branch('1.0')
        // Creating a build
        def build = ontrack.branch(project, '1.0').build('1', 'Build 1')
        // Setting the Jenkins build property
        build.properties {
            jenkinsBuild 'Local', 'JOB', 1
        }
        // Gets its Jenkins build property
        def property = build.properties.jenkinsBuild
        assert property.job == 'JOB'
        assert property.build == 1
    }

    @Test
    void 'Setting the Release property on a build'() {
        // Creating a branch
        def project = uid('P')
        ontrack.project(project).branch('1.0')
        // Creating a build
        def build = ontrack.branch(project, '1.0').build('1', 'Build 1')
        // Setting the Label property
        build.properties {
            label 'RC'
        }
        // Gets its Label property
        assert build.properties.label == 'RC'
    }

    @Test
    void 'Definition of a template with parameters'() {
        // GitHub configuration
        ontrack.configure {
            gitHub 'ontrack', repository: 'nemerosa/ontrack', indexationInterval: 0
        }
        // Project and branch template
        def project = uid('P')
        ontrack.project(project) {
            properties {
                gitHub 'ontrack'
            }
            branch('template') {
                promotionLevel 'COPPER', 'Copper promotion'
                promotionLevel 'BRONZE', 'Bronze promotion'
                validationStamp 'SMOKE', 'Smoke tests'
                // Git branch
                properties {
                    gitBranch '${gitBranch}'
                }
                // Template definition
                template {
                    parameter 'gitBranch', 'Name of the Git branch'
                }
            }
        }
        // Creates an instance
        ontrack.branch(project, 'template').instance 'TEST', [
                gitBranch: 'feature/test'
        ]
        // Checks the created instance
        def instance = ontrack.branch(project, 'TEST')
        assert instance.id > 0
        assert instance.name == 'TEST'
        // Checks the Git branch of the instance
        def property = instance.properties.gitBranch
        assert property.branch == 'feature/test'
    }

    @Test
    void 'Launching branch template synchronisation'() {
        // GitHub configuration
        ontrack.configure {
            gitHub 'ontrack', repository: 'nemerosa/ontrack', indexationInterval: 0
        }
        // Project and branch template
        def project = uid('P')
        ontrack.project(project) {
            properties {
                gitHub 'ontrack'
            }
            branch('template') {
                promotionLevel 'COPPER', 'Copper promotion'
                promotionLevel 'BRONZE', 'Bronze promotion'
                validationStamp 'SMOKE', 'Smoke tests'
                // Git branch
                properties {
                    gitBranch '${gitBranch}'
                }
                // Template definition
                template {
                    parameter 'gitBranch', 'Name of the Git branch', 'release/${sourceName}'
                    fixedSource '1.0', '1.1'
                }
            }
        }
        // Sync. the template
        ontrack.branch(project, 'template').sync()
        // Checks the created instances
        ['1.0', '1.1'].each {
            def instance = ontrack.branch(project, it)
            assert instance.id > 0
            assert instance.name == it
            // Checks the Git branch of the instance
            def property = instance.properties.gitBranch
            assert property.branch == "release/${it}"
        }
    }

    @Test
    void 'External script with local binding: getting the last promoted build'() {
        // Environment preparation
        def branch = createBuildsAndPromotions()
        def projectName = branch.project
        def branchName = branch.name

        // Script to execute
        def script = '''\
def build = ontrack.branch(project, branch).lastPromotedBuilds[0]?.name
local.put('BUILD', build)
'''
        // Local binding
        def local = [:]

        // Binding
        def binding = new Binding([
                ontrack: ontrack,
                local  : local,
                project: projectName,
                branch : branchName
        ]);

        // Shell
        def shell = new GroovyShell(binding)

        // Running the script
        shell.evaluate(script)

        // Checks the result
        assert local.BUILD == '3'
    }

    @Test
    void 'DSL shell: getting the last promoted build'() {
        // Environment preparation
        def branch = createBuildsAndPromotions()
        def projectName = branch.project
        def branchName = branch.name

        // Script to execute
        def script = '''\
def build = ontrack.branch(project, branch).lastPromotedBuilds[0]?.name
shell.put('BUILD', build)
'''

        // Script file
        def file = File.createTempFile('ontrack', '.dsl')
        file.text = script

        // Shell call
        def output = new StringWriter()
        def shell = Shell.create().withOutput(output)
        List<String> args = [
                '--url', baseURL, '--user', 'admin', '--password', adminPassword, '--file', file.absolutePath,
                '--value', "project=${projectName}" as String,
                '--value', "branch=${branchName}" as String
        ]
        if (sslDisabled) {
            args << '--no-ssl'
        }
        shell args

        // Gets the output
        def text = output.toString()
        assert text.trim() == 'BUILD=3'
    }

    protected Branch createBuildsAndPromotions() {
        // Creating a branch
        def testBranch = doCreateBranch()
        def projectName = testBranch.project.name.asText()
        def branchName = testBranch.name.asText()

        // Gets the branch
        def branch = ontrack.branch(projectName, branchName)
        assert branch.id > 0
        assert branch.project == projectName
        assert branch.name == branchName

        // Creating some promotion levels using the DSL
        branch.promotionLevel 'COPPER', 'Copper promotion'
        branch.promotionLevel 'BRONZE', 'Bronze promotion'
        branch.promotionLevel 'GOLD', 'Gold promotion'

        // Creating some validation stamps
        branch.validationStamp 'SMOKE', 'Smoke tests'
        branch.validationStamp 'REGRESSION', 'Regression tests'

        // Creating some builds
        def build1 = branch.build('1', 'Build 1')
        def build2 = branch.build('2', 'Build 2')
        def build3 = branch.build('3', 'Build 3')

        // Promoting the builds
        build1.promote 'GOLD'
        build2.promote 'BRONZE'
        build3.promote 'COPPER'

        // OK
        branch
    }

}
