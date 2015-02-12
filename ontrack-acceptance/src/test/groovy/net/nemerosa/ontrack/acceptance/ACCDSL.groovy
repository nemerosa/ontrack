package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.support.AcceptanceTest
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import net.nemerosa.ontrack.dsl.Branch
import net.nemerosa.ontrack.dsl.Ontrack
import net.nemerosa.ontrack.dsl.OntrackConnection
import net.nemerosa.ontrack.dsl.Shell
import net.nemerosa.ontrack.dsl.http.OTMessageClientException
import org.junit.Assert
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

    @Test
    void 'Branch not found'() {
        // Creating a branch
        def testBranch = doCreateBranch()
        def projectName = testBranch.project.name.asText()
        def branchName = testBranch.name.asText()
        // Anonymous client
        ontrack = OntrackConnection.create(baseURL).disableSsl(sslDisabled).build()
        // Branch cannot be found
        try {
            ontrack.branch(projectName, branchName)
            Assert.fail "Branch access should have been forbidden"
        } catch (OTMessageClientException ex) {
            assert ex.message == "Branch not found: ${projectName}/${branchName}"
        }
    }

    @Test
    void 'Getting last promoted build'() {
        def branch = createBuildsAndPromotions()
        // Getting the last promoted builds
        def results = branch.lastPromotedBuilds
        assert results.collect { it.name } == ['3', '2', '1']
    }

    @Test
    void 'Build search: default'() {
        Branch branch = createBuildsAndPromotions()
        def builds = ontrack.project(branch.project).search()
        assert builds.collect { it.name } == ['3', '2', '1']
    }

    @Test
    void 'Build search: count'() {
        Branch branch = createBuildsAndPromotions()
        def builds = ontrack.project(branch.project).search(maximumCount: 1)
        assert builds.collect { it.name } == ['3']
    }

    @Test
    void 'Build search: promotion'() {
        Branch branch = createBuildsAndPromotions()
        def builds = ontrack.project(branch.project).search(promotionName: 'BRONZE')
        assert builds.collect { it.name } == ['2']
    }

    @Test
    void 'Filtering build on promotion'() {
        Branch branch = createBuildsAndPromotions()
        // Filtering builds on promotion
        def results = branch.standardFilter withPromotionLevel: 'BRONZE'
        assert results.collect { it.name } == ['2']
    }

    @Test
    void 'Promotion runs'() {
        def branch = createBuildsAndPromotions()
        // Creates a run
        def run = ontrack.build(branch.project, branch.name, '2').promote('BRONZE')
        assert run.promotionLevel.name == 'BRONZE'
        // List of runs
        def runs = ontrack.build(branch.project, branch.name, '2').promotionRuns
        assert runs.size() == 2
        runs.each { assert it.promotionLevel.name == 'BRONZE' }
    }

    @Test
    void 'Validation runs'() {
        def branch = createBuildsAndPromotions()
        // Creates one run
        def run = ontrack.build(branch.project, branch.name, '2').validate('SMOKE', 'FAILED')
        assert run.validationStamp.name == 'SMOKE'
        assert run.validationRunStatuses[0].statusID.id == 'FAILED'
        assert run.validationRunStatuses[0].statusID.name == 'Failed'
        // Creates a second run
        ontrack.build(branch.project, branch.name, '2').validate('SMOKE')
        // List of runs
        def runs = ontrack.build(branch.project, branch.name, '2').validationRuns
        assert runs.size() == 2
        runs.each { assert it.validationStamp.name == 'SMOKE' }
        assert runs[0].validationRunStatuses[0].statusID.id == 'FAILED'
        assert runs[1].validationRunStatuses[0].statusID.id == 'PASSED'
        // Shortcuts
        assert runs[0].status == 'FAILED'
        assert runs[1].status == 'PASSED'
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
        build.config {
            jenkinsBuild 'Local', 'JOB', 1
        }
        // Gets its Jenkins build property
        def property = build.config.jenkinsBuild
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
        build.config {
            label 'RC'
        }
        // Gets its Label property
        assert build.config.label == 'RC'
    }

    protected static File getImageFile() {
        def file = File.createTempFile('image', '.png')
        file.bytes = ACCDSL.class.getResource('/gold.png').bytes
        file
    }

    @Test
    void 'Promotion level image: from file'() {
        def project = uid('P')
        ontrack.project(project) {
            branch('1.0') {
                promotionLevel('COPPER', 'Copper promotion') {
                    image getImageFile()
                }
            }
        }
        // Downloading the image
        def image = ontrack.promotionLevel(project, '1.0', 'COPPER').image
        assert image.type == 'image/png'
        assert image.content == imageFile.bytes
    }

    @Test
    void 'Promotion level image: from path'() {
        def project = uid('P')
        ontrack.project(project) {
            branch('1.0') {
                promotionLevel('COPPER', 'Copper promotion') {
                    image getImageFile().absolutePath
                }
            }
        }
        // Downloading the image
        def image = ontrack.promotionLevel(project, '1.0', 'COPPER').image
        assert image.type == 'image/png'
        assert image.content == imageFile.bytes
    }

    @Test
    void 'Promotion level image: from URL'() {
        def project = uid('P')
        ontrack.project(project) {
            branch('1.0') {
                promotionLevel('COPPER', 'Copper promotion') {
                    image ACCDSL.class.getResource('/gold.png')
                }
            }
        }
        // Downloading the image
        def image = ontrack.promotionLevel(project, '1.0', 'COPPER').image
        assert image.type == 'image/png'
        assert image.content == imageFile.bytes
    }

    @Test
    void 'Promotion level image: from URL path'() {
        def project = uid('P')
        ontrack.project(project) {
            branch('1.0') {
                promotionLevel('COPPER', 'Copper promotion') {
                    image ACCDSL.class.getResource('/gold.png').toString()
                }
            }
        }
        // Downloading the image
        def image = ontrack.promotionLevel(project, '1.0', 'COPPER').image
        assert image.type == 'image/png'
        assert image.content == imageFile.bytes
    }

    @Test
    void 'Validation stamp image: from file'() {
        def project = uid('P')
        ontrack.project(project) {
            branch('1.0') {
                validationStamp('SMOKE', 'Smoke tests') {
                    image getImageFile()
                }
            }
        }
        // Downloading the image
        def image = ontrack.validationStamp(project, '1.0', 'SMOKE').image
        assert image.type == 'image/png'
        assert image.content == imageFile.bytes
    }

    @Test
    void 'Validation stamp image: from path'() {
        def project = uid('P')
        ontrack.project(project) {
            branch('1.0') {
                validationStamp('SMOKE', 'Smoke tests') {
                    image getImageFile().absolutePath
                }
            }
        }
        // Downloading the image
        def image = ontrack.validationStamp(project, '1.0', 'SMOKE').image
        assert image.type == 'image/png'
        assert image.content == imageFile.bytes
    }

    @Test
    void 'Validation stamp image: from URL'() {
        def project = uid('P')
        ontrack.project(project) {
            branch('1.0') {
                validationStamp('SMOKE', 'Smoke tests') {
                    image ACCDSL.class.getResource('/gold.png')
                }
            }
        }
        // Downloading the image
        def image = ontrack.validationStamp(project, '1.0', 'SMOKE').image
        assert image.type == 'image/png'
        assert image.content == imageFile.bytes
    }

    @Test
    void 'Validation stamp image: from URL path'() {
        def project = uid('P')
        ontrack.project(project) {
            branch('1.0') {
                validationStamp('SMOKE', 'Smoke tests') {
                    image ACCDSL.class.getResource('/gold.png').toString()
                }
            }
        }
        // Downloading the image
        def image = ontrack.validationStamp(project, '1.0', 'SMOKE').image
        assert image.type == 'image/png'
        assert image.content == imageFile.bytes
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
            config {
                gitHub 'ontrack'
            }
            branch('template') {
                promotionLevel 'COPPER', 'Copper promotion'
                promotionLevel 'BRONZE', 'Bronze promotion'
                validationStamp 'SMOKE', 'Smoke tests'
                // Git branch
                config {
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
        def property = instance.config.gitBranch
        assert property.branch == 'feature/test'
    }

    @Test
    void 'Unlinking a template instance'() {
        // Project and branch template
        def project = uid('P')
        ontrack.project(project) {
            branch('template') {
                template {
                    parameter 'paramName', 'A parameter'
                }
            }
        }
        // Creates an instance
        ontrack.branch(project, 'template').instance 'TEST', [
                paramName: 'paramValue'
        ]
        // Checks the created instance
        assert ontrack.branch(project, 'TEST').type == 'TEMPLATE_INSTANCE'
        // Unlinks
        ontrack.branch(project, 'TEST').unlink()
        // Checks the unlinked instance
        assert ontrack.branch(project, 'TEST').type == 'CLASSIC'
    }

    @Test
    void 'Project property - JIRA follow links'() {
        def name = uid('P')
        ontrack.project(name) {
            config {
                jiraFollowLinks 'Clones', 'Depends'
            }
        }
        assert ontrack.project(name).config.jiraFollowLinks == ['Clones', 'Depends']
    }

    @Test
    void 'Project property - SVN configuration'() {
        def name = uid('S')
        ontrack.configure {
            svn name, url: 'svn://localhost'
        }
        def project = uid('P')
        ontrack.project(project) {
            config {
                svn name, '/project/trunk'
            }
        }
        def cfg = ontrack.project(project).config.svn
        assert cfg.configuration.name == name
        assert cfg.projectPath == '/project/trunk'
    }

    @Test
    void 'Project property - SVN configuration with GString'() {
        def name = uid('S')
        ontrack.configure {
            svn name, url: 'svn://localhost'
        }
        def project = uid('P')
        ontrack.project(project) {
            config {
                svn name, "/${name}/trunk"
            }
        }
        def cfg = ontrack.project(project).config.svn
        assert cfg.configuration.name == name
        assert cfg.projectPath == "/${name}/trunk"
    }

    @Test
    void 'Project property - Git configuration'() {
        def name = uid('G')
        ontrack.configure {
            git name, remote: 'https://github.com/nemerosa/ontrack.git', user: 'test', password: 'secret'
        }
        def project = uid('P')
        ontrack.project(project) {
            config {
                git name
            }
        }
        def cfg = ontrack.project(project).config.git
        assert cfg.configuration.name == name
    }

    @Test
    void 'Link property'() {
        def project = uid('P')
        ontrack.project(project) {
            config {
                links 'project': 'http://project'
            }
            branch('test') {
                config {
                    links 'branch': 'http://branch'
                }
                build('1') {
                    config {
                        links 'build': 'http://build'
                    }
                }
            }
        }
        assert ontrack.project(project).config.links.project == 'http://project'
        assert ontrack.branch(project, 'test').config.links.branch == 'http://branch'
        assert ontrack.build(project, 'test', '1').config.links.build == 'http://build'
    }

    @Test
    void 'Build Git commit property'() {
        def name = uid('P')
        ontrack.project(name) {
            branch('test') {
                build('1') {
                    config {
                        gitCommit 'adef13'
                    }
                }
            }
        }
        assert ontrack.build(name, 'test', '1').config.gitCommit == 'adef13'
    }

    @Test
    void 'Jenkins build property'() {
        ontrack.configure {
            jenkins 'Jenkins', 'http://jenkins'
        }
        def name = uid('P')
        def project = ontrack.project(name)
        def branch = project.branch('test') {
            promotionLevel('COPPER')
            validationStamp('TEST')
        }
        def build = branch.build('1') {
            config {
                jenkinsBuild 'Jenkins', 'MyBuild', 1
            }
            promote('COPPER') {
                config {
                    jenkinsBuild 'Jenkins', 'MyPromotion', 1
                }
            }
            validate('TEST') {
                config {
                    jenkinsBuild 'Jenkins', 'MyValidation', 1
                }
            }
        }

        def j = ontrack.build(name, 'test', '1').config.jenkinsBuild
        assert j.configuration.name == 'Jenkins'
        assert j.job == 'MyBuild'
        assert j.build == 1
        assert j.url == 'http://jenkins/job/MyBuild/1'

        // Promotion run build

        j = ontrack.build(name, 'test', '1').promotionRuns[0].config.jenkinsBuild
        assert j.configuration.name == 'Jenkins'
        assert j.job == 'MyPromotion'
        assert j.build == 1
        assert j.url == 'http://jenkins/job/MyPromotion/1'

        // Validation run build

        j = ontrack.build(name, 'test', '1').validationRuns[0].config.jenkinsBuild
        assert j.configuration.name == 'Jenkins'
        assert j.job == 'MyValidation'
        assert j.build == 1
        assert j.url == 'http://jenkins/job/MyValidation/1'
    }

    @Test
    void 'Jenkins job property'() {
        ontrack.configure {
            jenkins 'Jenkins', 'http://jenkins'
        }
        def name = uid('P')
        ontrack.project(name) {
            config {
                jenkinsJob 'Jenkins', 'MyProject'
            }
            branch('test') {
                config {
                    jenkinsJob 'Jenkins', 'MyBranch'
                }
                promotionLevel('COPPER') {
                    config {
                        jenkinsJob 'Jenkins', 'MyPromotion'
                    }
                }
                validationStamp('TEST') {
                    config {
                        jenkinsJob 'Jenkins', 'MyValidation'
                    }
                }
            }
        }

        def j = ontrack.project(name).config.jenkinsJob
        assert j.configuration.name == 'Jenkins'
        assert j.job == 'MyProject'
        assert j.url == 'http://jenkins/job/MyProject'

        j = ontrack.branch(name, 'test').config.jenkinsJob
        assert j.configuration.name == 'Jenkins'
        assert j.job == 'MyBranch'
        assert j.url == 'http://jenkins/job/MyBranch'

        j = ontrack.promotionLevel(name, 'test', 'COPPER').config.jenkinsJob
        assert j.configuration.name == 'Jenkins'
        assert j.job == 'MyPromotion'
        assert j.url == 'http://jenkins/job/MyPromotion'

        j = ontrack.validationStamp(name, 'test', 'TEST').config.jenkinsJob
        assert j.configuration.name == 'Jenkins'
        assert j.job == 'MyValidation'
        assert j.url == 'http://jenkins/job/MyValidation'

    }

    @Test
    void 'Jenkins job property URL with folders'() {
        ontrack.configure {
            jenkins 'Jenkins', 'http://jenkins'
        }
        def name = uid('P')
        ontrack.project(name) {
            config {
                jenkinsJob 'Jenkins', 'prj/prj-build'
            }
        }
        assert ontrack.project(name).config.jenkinsJob.url == 'http://jenkins/job/prj/job/prj-build'
        ontrack.project(name) {
            config {
                jenkinsJob 'Jenkins', 'prj/job/prj-build'
            }
        }
        assert ontrack.project(name).config.jenkinsJob.url == 'http://jenkins/job/prj/job/prj-build'
    }

    @Test
    void 'Jenkins build property URL with folders'() {
        ontrack.configure {
            jenkins 'Jenkins', 'http://jenkins'
        }
        def name = uid('P')
        ontrack.project(name) {
            branch('test') {
                build('1') {
                    config {
                        jenkinsBuild 'Jenkins', 'prj/prj-test/prj-test-build', 1
                    }
                }
            }
        }
        assert ontrack.build(name, 'test', '1').config.jenkinsBuild.url == 'http://jenkins/job/prj/job/prj-test/job/prj-test-build/1'

        ontrack.branch(name, 'test').build('2') {
            config {
                jenkinsBuild 'Jenkins', 'prj/job/prj-test/job/prj-test-build', 2
            }
        }
        assert ontrack.build(name, 'test', '2').config.jenkinsBuild.url == 'http://jenkins/job/prj/job/prj-test/job/prj-test-build/2'
    }

    @Test
    void 'Branch property - SVN configuration'() {
        def name = uid('S')
        ontrack.configure {
            svn name, url: 'svn://localhost'
        }
        def project = uid('P')
        ontrack.project(project) {
            config {
                svn name, '/project/trunk'
            }
            branch('mybranch') {
                config {
                    svn '/project/branches/mybranch', '/project/tags/{build:mybranch-*}'
                }
            }
        }
        def cfg = ontrack.branch(project, 'mybranch').config.svn
        assert cfg.branchPath == '/project/branches/mybranch'
        assert cfg.buildPath == '/project/tags/{build:mybranch-*}'
    }

    @Test
    void 'Branch property - SVN Validator - closed issues'() {
        def name = uid('S')
        ontrack.configure {
            svn name, url: 'svn://localhost'
        }
        def project = uid('P')
        ontrack.project(project) {
            config {
                svn name, '/project/trunk'
            }
            branch('test') {
                config {
                    svn '/project/branches/mybranch', '/project/tags/{build:mybranch-*}'
                    svnValidatorClosedIssues(['Closed'])
                }
            }
        }
        assert ontrack.branch(project, 'test').config.svnValidatorClosedIssues.closedStatuses == ['Closed']
    }

    @Test
    void 'Branch property - SVN sync'() {
        def name = uid('S')
        ontrack.configure {
            svn name, url: 'svn://localhost'
        }
        def project = uid('P')
        ontrack.project(project) {
            config {
                svn name, '/project/trunk'
            }
            branch('test') {
                config {
                    svn '/project/branches/mybranch', '/project/tags/{build:mybranch-*}'
                    svnSync 30
                }
            }
        }
        def sync = ontrack.branch(project, 'test').config.svnSync
        assert sync.override == false
        assert sync.interval == 30
    }

    @Test
    void 'Branch property - Artifactory sync'() {
        def name = uid('A')
        ontrack.configure {
            artifactory name, 'http://artifactory'
        }
        def project = uid('P')
        ontrack.project(project) {
            branch('test') {
                config {
                    artifactorySync name, 'test', 'test-*', 30
                }
            }
        }
        def sync = ontrack.branch(project, 'test').config.artifactorySync
        assert sync.configuration.name == name
        assert sync.buildName == 'test'
        assert sync.buildNameFilter == 'test-*'
        assert sync.interval == 30
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
            config {
                gitHub 'ontrack'
            }
            branch('template') {
                promotionLevel 'COPPER', 'Copper promotion'
                promotionLevel 'BRONZE', 'Bronze promotion'
                validationStamp 'SMOKE', 'Smoke tests'
                // Git branch
                config {
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
            def property = instance.config.gitBranch
            assert property.branch == "release/${it}"
        }
    }

    @Test
    void 'Configuration - GitHub'() {
        def name = uid('GH')
        ontrack.configure {
            gitHub name, repository: 'nemerosa/ontrack', indexationInterval: 0, oauth2Token: 'ABCDEF'
        }
        assert ontrack.config.gitHub.find { it == name } != null
    }

    @Test
    void 'Configuration - Git'() {
        def name = uid('G')
        ontrack.configure {
            git name, remote: 'https://github.com/nemerosa/ontrack.git', user: 'test', password: 'secret'
        }
        assert ontrack.config.git.find { it == name } != null
    }

    @Test
    void 'Configuration - Jenkins'() {
        def name = uid('J')
        ontrack.configure {
            jenkins name, 'http://jenkins'
        }
        assert ontrack.config.jenkins.find { it == name } != null
    }

    @Test
    void 'Configuration - JIRA'() {
        def name = uid('J')
        ontrack.configure {
            jira name, 'http://jira'
        }
        assert ontrack.config.jira.find { it == name } != null
    }

    @Test
    void 'Configuration - JIRA and Git'() {
        def jiraName = uid('J')
        def gitName = uid('G')
        ontrack.configure {
            jira jiraName, 'http://jira'
            git gitName, remote: 'https://github.com/nemerosa/ontrack.git', user: 'test', password: 'secret', issueServiceConfigurationIdentifier: "jira:${jiraName}"
        }
    }

    @Test
    void 'Configuration - Artifactory'() {
        def name = uid('A')
        ontrack.configure {
            artifactory name, 'http://artifactory'
        }
        assert ontrack.config.artifactory.find { it == name } != null
    }

    @Test
    void 'Configuration - SVN'() {
        ontrack.configure {
            svn uid('S'), url: 'svn://localhost'
        }
    }

    @Test
    void 'Configuration - SVN - update'() {
        def name = uid('S')
        ontrack.configure {
            svn name, url: 'svn://localhost'
        }
        assert ontrack.config.svn.findAll { it == name } == [name]
        ontrack.configure {
            svn name, url: 'http://localhost'
        }
        assert ontrack.config.svn.findAll { it == name } == [name]
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
    void 'External script with local binding: project template'() {
        // Environment preparation
        def name = uid('N')
        def project = uid('P')

        // Script to execute
        def script = '''\
def confName = NAME
ontrack.configure {
    svn confName, url: 'svn://localhost\'
}
ontrack.project(PROJECT) {
    println "name=${confName}"
    config {
        svn confName, "/${confName}/trunk"
    }
}
'''
        // Binding
        def binding = new Binding([
                ontrack: ontrack,
                PROJECT: project,
                NAME   : name
        ]);

        // Shell
        def shell = new GroovyShell(binding)

        // Running the script
        shell.evaluate(script)

        // Gets the project's configuration back
        def cfg = ontrack.project(project).config.svn
        assert cfg.configuration.name == name
        assert cfg.projectPath == "/${name}/trunk"
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
