package net.nemerosa.ontrack.acceptance.tests.dsl

import net.nemerosa.ontrack.acceptance.AbstractACCDSL
import net.nemerosa.ontrack.acceptance.AcceptanceTestContext
import net.nemerosa.ontrack.acceptance.support.AcceptanceTest
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import net.nemerosa.ontrack.dsl.v4.Branch
import net.nemerosa.ontrack.dsl.v4.MetaInfo
import net.nemerosa.ontrack.dsl.v4.ObjectAlreadyExistsException
import net.nemerosa.ontrack.dsl.v4.http.OTForbiddenClientException
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

/**
 * Ontrack DSL tests.
 */
@AcceptanceTestSuite
@AcceptanceTest([AcceptanceTestContext.SMOKE, AcceptanceTestContext.VAULT])
class ACCDSL extends AbstractACCDSL {

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
    void 'Build previous and next'() {
        // Project and branch
        def name = uid('P')
        def project = ontrack.project(name)
        def branch = project.branch('master', '')
        // Builds
        def build1 = branch.build('1', '')
        def build2 = branch.build('2', '')
        def build3 = branch.build('3', '')
        // Build 1 has no previous build
        assert build1.previousBuild == null
        assert build1.nextBuild.name == '2'
        // Build 2 has previous and next builds
        assert build2.previousBuild.name == '1'
        assert build2.nextBuild.name == '3'
        // Build 3 has no next build
        assert build3.previousBuild.name == '2'
        assert build3.nextBuild == null
    }

    @Test
    void 'Build update'() {
        // Project and branch
        def name = uid('P')
        def project = ontrack.project(name)
        def branch = project.branch('master', '')
        // Creates a build
        branch.build('1', 'Build 1')
        // Updates the build
        branch.build('1', 'Build 2', true)
    }

    @Test
    void 'Build creation twice'() {
        // Project and branch
        def name = uid('P')
        def project = ontrack.project(name)
        def branch = project.branch('master', '')
        // Creates a build
        branch.build('1', 'Build 1')
        // Creates the build a second time
        try {
            branch.build('1', 'Build 2')
            assert false: 'The build creation should have failed'
        } catch (ObjectAlreadyExistsException ex) {
            assert ex.message == 'Build 1 already exists.'
        }
    }

    @Test
    void 'Filtering build on promotion'() {
        Branch branch = createBuildsAndPromotions()
        // Filtering builds on promotion
        def results = branch.standardFilter withPromotionLevel: 'BRONZE'
        assert results.collect { it.name } == ['2']
    }

    @Test
    void 'Filtering build - with validation (any)'() {
        Branch branch = createBuildsAndPromotions()
        ontrack.build(branch.project, branch.name, '2').validate('SMOKE', 'FAILED')
        ontrack.build(branch.project, branch.name, '3').validate('SMOKE', 'PASSED')
        def results = branch.standardFilter withValidationStamp: 'SMOKE'
        assert results.collect { it.name } == ['3', '2']
    }

    @Test
    void 'Filtering build - with validation (passed)'() {
        Branch branch = createBuildsAndPromotions()
        ontrack.build(branch.project, branch.name, '2').validate('SMOKE', 'FAILED')
        ontrack.build(branch.project, branch.name, '3').validate('SMOKE', 'PASSED')
        def results = branch.standardFilter withValidationStamp: 'SMOKE', withValidationStampStatus: 'PASSED'
        assert results.collect { it.name } == ['3']
    }

    @Test
    void 'Filtering build - since validation (any)'() {
        Branch branch = createBuildsAndPromotions()
        ontrack.build(branch.project, branch.name, '1').validate('SMOKE', 'PASSED')
        ontrack.build(branch.project, branch.name, '2').validate('SMOKE', 'FAILED')
        def results = branch.standardFilter sinceValidationStamp: 'SMOKE'
        assert results.collect { it.name } == ['3', '2']
    }

    @Test
    void 'Filtering build - since validation (passed)'() {
        Branch branch = createBuildsAndPromotions()
        ontrack.build(branch.project, branch.name, '1').validate('SMOKE', 'PASSED')
        ontrack.build(branch.project, branch.name, '2').validate('SMOKE', 'FAILED')
        def results = branch.standardFilter sinceValidationStamp: 'SMOKE', sinceValidationStampStatus: 'PASSED'
        assert results.collect { it.name } == ['3', '2', '1']
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
    void 'Promotion run deletion'() {
        def branch = createBuildsAndPromotions()
        // Creates two runs
        ontrack.build(branch.project, branch.name, '2').promote('BRONZE')
        ontrack.build(branch.project, branch.name, '2').promote('BRONZE')
        // Gets the promotion runs
        def runs = ontrack.build(branch.project, branch.name, '2').promotionRuns
        def run = runs.get(0)
        def deleteLink = run.link('delete')
        assert deleteLink == "/rest/structure/promotionRuns/${run.id}" as String
    }

    @Test
    void 'Branch disabled flag'() {
        def branch = createBranch()
        // Checks the branch is not disabled
        assertFalse("Branch is not disabled", branch.disabled)
        // Disables the branch
        branch.disable()
        branch = ontrack.branch(branch.project, branch.name)
        assertTrue("Branch is disabled", branch.disabled)
        // Enables the branch
        branch.enable()
        branch = ontrack.branch(branch.project, branch.name)
        assertFalse("Branch is not disabled", branch.disabled)
    }

    @Test
    void 'Branch validation stamps'() {
        def name = uid('P')
        ontrack.project(name) {
            branch('1.0') {
                validationStamp 'CI'
                validationStamp 'QA'
            }
        }
        assert ontrack.branch(name, '1.0').validationStamps*.name == ['CI', 'QA']
    }

    @Test
    void 'Branch validation stamps with complex names'() {
        def name = uid('P')
        ontrack.project(name) {
            branch('1.0') {
                validationStamp 'CI.J2EE.JBOSS'
                validationStamp 'GEN.CSS'
            }
        }
        assert ontrack.branch(name, '1.0').validationStamps*.name == ['CI.J2EE.JBOSS', 'GEN.CSS']
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
        assert runs[0].validationRunStatuses[0].statusID.id == 'PASSED'
        assert runs[1].validationRunStatuses[0].statusID.id == 'FAILED'
        // Shortcuts
        assert runs[0].status == 'PASSED'
        assert runs[1].status == 'FAILED'
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
        // Checks the project does exist
        def p = ontrack.project(project)
        assert p.name == project
        assert p.description == ''
        // Checks the branch does exist
        def branch = ontrack.branch(project, '1.0')
        assert branch.name == '1.0'
        assert branch.description == ''
        // Checks the structure
        assert ontrack.promotionLevel(project, '1.0', 'COPPER').name == 'COPPER'
        assert ontrack.validationStamp(project, '1.0', 'SMOKE').name == 'SMOKE'
    }

    @Test
    void 'Definition of a project and a branch with description'() {
        def project = uid('P')
        ontrack.project(project, 'My project') {
            branch('1.0', 'My branch') {
                promotionLevel 'COPPER', 'Copper promotion'
                validationStamp 'SMOKE', 'Smoke tests'
            }
        }
        // Checks the project does exist
        def p = ontrack.project(project)
        assert p.name == project
        assert p.description == 'My project'
        // Checks the branch does exist
        def branch = ontrack.branch(project, '1.0')
        assert branch.name == '1.0'
        assert branch.description == 'My branch'
        // Checks the structure
        assert ontrack.promotionLevel(project, '1.0', 'COPPER').name == 'COPPER'
        assert ontrack.validationStamp(project, '1.0', 'SMOKE').name == 'SMOKE'
    }

    @Test(expected = OTForbiddenClientException)
    void 'Definition of a project is not granted for a controller'() {
        // Creation of a controller
        def userName = uid('A')
        doCreateController(userName, 'pwd')
        // Connects using this controller
        ontrack = getOntrackAs(userName, 'pwd')
        // Creation of the project and branch
        ontrack.project(uid('P'))
    }

    @Test
    void 'Definition of a project is granted for a creator'() {
        // Creation of a controller
        def userName = uid('A')
        doCreateCreator(userName, 'pwd')
        // Connects using this controller
        ontrack = getOntrackAs(userName, 'pwd')
        // Creation of the project and branch
        assert ontrack.project(uid('P')).id > 0
    }

    @Test
    void 'Definition of a project is granted for an automation role'() {
        // Creation of a controller
        def userName = uid('A')
        doCreateAutomation(userName, 'pwd')
        // Connects using this controller
        ontrack = getOntrackAs(userName, 'pwd')
        // Creation of the project and branch
        assert ontrack.project(uid('P')).id > 0
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
    void 'Message property'() {
        def project = uid('P')
        ontrack.project(project) {
            config {
                message 'Information'
            }
            branch('test') {
                config {
                    message 'Warning', 'WARNING'
                }
                build('1') {
                    config {
                        message 'Error', 'ERROR'
                    }
                }
            }
        }
        assert ontrack.project(project).config.message == [type: 'INFO', text: 'Information']
        assert ontrack.branch(project, 'test').config.message == [type: 'WARNING', text: 'Warning']
        assert ontrack.build(project, 'test', '1').config.message == [type: 'ERROR', text: 'Error']
    }

    @Test
    void 'Meta info property'() {
        def project = uid('P')
        ontrack.project(project) {
            config {
                metaInfo A: '1', B: '2'
            }
            branch('test') {
                config {
                    metaInfo 'A', '1', 'linkA'
                    metaInfo 'B', '2', 'linkB'
                    metaInfo 'C', '3', 'linkC', 'CatC'
                }
            }
        }
        assert ontrack.project(project).config.metaInfo == [
                new MetaInfo('A', '1', null, null),
                new MetaInfo('B', '2', null, null),
        ]

        def metaInfos = ontrack.branch(project, 'test').config.metaInfo
        assert metaInfos.size() == 3
        assert metaInfos[0] == new MetaInfo('A', '1', 'linkA', null)
        assert metaInfos[1] == new MetaInfo('B', '2', 'linkB', null)
        assert metaInfos[2] == new MetaInfo('C', '3', 'linkC', 'CatC')
    }

    @Test
    void 'Search on meta info property'() {
        def project = uid('P')
        ontrack.project(project) {
            branch('test') {
                build('1', 'Build 1') {
                    config {
                        metaInfo 'A', '1', 'link/1'
                    }
                }
                build('2', 'Build 2') {
                    config {
                        metaInfo 'A', '2', 'link/2'
                    }
                }
            }
        }

        def results = ontrack.branch(project, 'test').standardFilter([
                withProperty     : 'net.nemerosa.ontrack.extension.general.MetaInfoPropertyType',
                withPropertyValue: 'A:2'
        ])
        assert results.collect { it.name } == ['2']
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
    void 'Configuration - Stash'() {
        def name = uid('S')
        ontrack.configure {
            stash name, url: 'http://localhost:443/stash'
        }
        assert ontrack.config.stash.find { it == name } != null
    }

    @Test
    void 'Project configuration - Stash'() {
        def name = uid('S')
        ontrack.configure {
            stash name, url: 'http://localhost:443/stash'
        }
        def projectName = uid('P')
        // Sets the configuration
        ontrack.project(projectName) {
            config {
                stash(name, 'PROJECT', 'my-repository')
            }
        }
        // Gets the configuration
        def stash = ontrack.project(projectName).config.stash
        assert stash.configuration.name == name
        assert stash.project == 'PROJECT'
        assert stash.repository == 'my-repository'
        assert stash.repositoryUrl == 'http://localhost:443/stash/projects/PROJECT/repos/my-repository'
    }

    @Test
    void 'Configuration - GitHub'() {
        def name = uid('GH')
        ontrack.configure {
            gitHub name, oauth2Token: 'ABCDEF'
        }
        assert ontrack.config.gitHub.find { it == name } != null
    }

    @Test
    void 'Configuration - GitLab'() {
        def name = uid('GL')
        ontrack.configure {
            gitLab name, url: 'https://gitlab.acme.com', user: 'user', password: 'abcdef'
        }
        assert ontrack.config.gitLab.find { it == name } != null
    }

    @Test
    void 'Project configuration - GitLab'() {
        def name = uid('GL')
        ontrack.configure {
            gitLab name, url: 'https://gitlab.acme.com', user: 'user', password: 'abcdef'
        }
        def projectName = uid('P')
        // Sets the configuration
        ontrack.project(projectName) {
            config {
                gitLab name, repository: "nemerosa/ontrack", indexationInterval: 10, issueServiceConfigurationIdentifier: 'self'
            }
        }
        // Gets the configuration
        def gitLab = ontrack.project(projectName).config.gitLab
        assert gitLab != null
        assert gitLab.configuration.name == name
        assert gitLab.repository == 'nemerosa/ontrack'
        assert gitLab.issueServiceConfigurationIdentifier == 'self'
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
            git gitName, remote: 'https://github.com/nemerosa/ontrack.git', user: 'test', password: 'secret', issueServiceConfigurationIdentifier: "jira//${jiraName}"
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
    void 'Change build signature'() {
        // Creates a build
        def name = uid('P')
        ontrack.project(name) {
            branch('Test') {
                build('1')
            }
        }
        // Changes the signature
        Date date = new Date()
        ontrack.build(name, 'Test', '1').signature 'Other', date
        // Checks the build
        def signature = ontrack.build(name, 'Test', '1').node.signature
        assert signature.user.name == 'Other'
        // I want the JDK8 Date/time API in Groovy :'(
        // assert signature.time == date
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

    protected Branch createBranch() {
        def testBranch = doCreateBranch()
        def projectName = testBranch.project.name.asText() as String
        def branchName = testBranch.name.asText() as String
        return ontrack.branch(projectName, branchName)
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
