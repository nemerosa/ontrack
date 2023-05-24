package net.nemerosa.ontrack.extension.git.resource

import com.fasterxml.jackson.databind.JsonNode
import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.extension.git.model.*
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.extension.issues.mock.TestIssueServiceConfiguration
import net.nemerosa.ontrack.extension.scm.model.SCMBuildView
import net.nemerosa.ontrack.it.MockSecurityService
import net.nemerosa.ontrack.json.JsonUtils
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.ui.controller.MockURIBuilder
import net.nemerosa.ontrack.ui.resource.DefaultResourceContext
import net.nemerosa.ontrack.ui.resource.ResourceObjectMapper
import net.nemerosa.ontrack.ui.resource.ResourceObjectMapperFactory
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

class GitChangeLogResourceDecoratorTest {

    private lateinit var mapper: ResourceObjectMapper
    private lateinit var gitService: GitService

    @BeforeEach
    fun before() {
        val securityService: SecurityService = MockSecurityService()
        gitService = mockk<GitService>()
        mapper = ResourceObjectMapperFactory().resourceObjectMapper(
                DefaultResourceContext(MockURIBuilder(), securityService),
                GitChangeLogResourceDecorator(gitService)
        )
    }

    @Test
    fun gitChangeLogWithIssues() {
        val signature = Signature.of(LocalDateTime.of(2014, 12, 5, 21, 53), "user")
        val project: Project = Project.of(NameDescription.nd("P", "Project")).withId(ID.of(1)).withSignature(signature)
        val branch: Branch = Branch.of(project, NameDescription.nd("B", "Branch")).withId(ID.of(10)).withSignature(signature)
        val buildView: List<BuildView> = listOf(1, 2)
                .map { it: Int ->
                    BuildView.of(
                            Build.of(
                                    branch,
                                    NameDescription.nd(it.toString(), "Build $it"),
                                    signature
                            ).withId(
                                    ID.of(it)
                            )
                    )
                }
        val changeLog = GitChangeLog(
                "uuid",
                project,
                SCMBuildView<GitBuildInfo>(
                        buildView[0],
                        GitBuildInfo.INSTANCE
                ),
                SCMBuildView<GitBuildInfo>(
                        buildView[1],
                        GitBuildInfo.INSTANCE
                ),
                false
        )
        every {
            gitService.getProjectConfiguration(project)
        } returns BasicGitActualConfiguration(
                BasicGitConfiguration.empty().withName("MyConfig").withIssueServiceConfigurationIdentifier("mock:MyTest"),
                TestIssueServiceConfiguration.configuredIssueService("MyTest")
        )
        val signatureObject = JsonUtils.`object`()
                .with("time", "2014-12-05T21:53:00Z")
                .with("user", JsonUtils.`object`()
                        .with("name", "user")
                        .end())
                .end()
        assertResourceJson(
                mapper,
                JsonUtils.`object`()
                        .with("project", JsonUtils.`object`()
                                .with("id", 1)
                                .with("name", "P")
                                .with("description", "Project")
                                .with("disabled", false)
                                .with("signature", signatureObject)
                                .end())
                        .with("scmBuildFrom", JsonUtils.`object`()
                                .with("buildView", JsonUtils.`object`()
                                        .with("build", JsonUtils.`object`()
                                                .with("id", 1)
                                                .with("name", "1")
                                                .with("description", "Build 1")
                                                .with("signature", signatureObject)
                                                .with("branch", JsonUtils.`object`()
                                                        .with("id", 10)
                                                        .with("name", "B")
                                                        .with("description", "Branch")
                                                        .with("disabled", false)
                                                        .with("signature", signatureObject)
                                                        .end())
                                                .end())
                                        .with("decorations", JsonUtils.array().end())
                                        .with("promotionRuns", JsonUtils.array().end())
                                        .with("validationStampRunViews", JsonUtils.array().end())
                                        .end())
                                .with("scm", JsonUtils.`object`()
                                        .with("placeholder", "")
                                        .end())
                                .end())
                        .with("scmBuildTo", JsonUtils.`object`()
                                .with("buildView", JsonUtils.`object`()
                                        .with("build", JsonUtils.`object`()
                                                .with("id", 2)
                                                .with("name", "2")
                                                .with("description", "Build 2")
                                                .with("signature", signatureObject)
                                                .with("branch", JsonUtils.`object`()
                                                        .with("id", 10)
                                                        .with("name", "B")
                                                        .with("description", "Branch")
                                                        .with("disabled", false)
                                                        .with("signature", signatureObject)
                                                        .end())
                                                .end())
                                        .with("decorations", JsonUtils.array().end())
                                        .with("promotionRuns", JsonUtils.array().end())
                                        .with("validationStampRunViews", JsonUtils.array().end())
                                        .end())
                                .with("scm", JsonUtils.`object`()
                                        .with("placeholder", "")
                                        .end())
                                .end())
                        .with("syncError", false)
                        .with("uuid", "uuid")
                        .with("_commits", "urn:test:net.nemerosa.ontrack.extension.git.GitController#changeLogCommits:uuid")
                        .with("_issues", "urn:test:net.nemerosa.ontrack.extension.git.GitController#changeLogIssues:uuid")
                        .with("_issuesIds", "urn:test:net.nemerosa.ontrack.extension.git.GitController#changeLogIssuesIds:uuid")
                        .with("_files", "urn:test:net.nemerosa.ontrack.extension.git.GitController#changeLogFiles:uuid")
                        .with("_changeLogFileFilters", "urn:test:net.nemerosa.ontrack.extension.scm.SCMController#getChangeLogFileFilters:1")
                        .with("_diff", "urn:test:net.nemerosa.ontrack.extension.git.GitController#diff:")
                        .with("_exportFormats", "urn:test:net.nemerosa.ontrack.extension.git.GitController#changeLogExportFormats:1")
                        .with("_exportIssues", "urn:test:net.nemerosa.ontrack.extension.git.GitController#changeLog:IssueChangeLogExportRequest%28format%3D%27text%27%2C+grouping%3D%27%27%2C+exclude%3D%27%27%2C+altGroup%3D%27Other%27%29")
                        .end(),
                changeLog
        )
    }

    @Test
    fun gitChangeLogWithoutIssues() {
        val signature = Signature.of(LocalDateTime.of(2014, 12, 5, 21, 53), "user")
        val project: Project = Project.of(NameDescription.nd("P", "Project")).withId(ID.of(1)).withSignature(signature)
        val branch: Branch = Branch.of(project, NameDescription.nd("B", "Branch")).withId(ID.of(10)).withSignature(signature)
        val buildView: List<BuildView> = listOf(1, 2)
                .map { it: Int ->
                    BuildView.of(
                            Build.of(
                                    branch,
                                    NameDescription.nd(it.toString(), "Build $it"),
                                    signature
                            ).withId(ID.of(it))
                    )
                }
        val changeLog = GitChangeLog(
                "uuid",
                project,
                SCMBuildView<GitBuildInfo>(
                        buildView[0],
                        GitBuildInfo.INSTANCE
                ),
                SCMBuildView<GitBuildInfo>(
                        buildView[1],
                        GitBuildInfo.INSTANCE
                ),
                false
        )
        every {
            gitService.getProjectConfiguration(project)
        } returns
                BasicGitActualConfiguration(
                        BasicGitConfiguration.empty().withName("MyConfig").withIssueServiceConfigurationIdentifier("mock:MyTest"),
                        null
                )
        val signatureObject = JsonUtils.`object`()
                .with("time", "2014-12-05T21:53:00Z")
                .with("user", JsonUtils.`object`()
                        .with("name", "user")
                        .end())
                .end()
        assertResourceJson(
                mapper,
                JsonUtils.`object`()
                        .with("project", JsonUtils.`object`()
                                .with("id", 1)
                                .with("name", "P")
                                .with("description", "Project")
                                .with("disabled", false)
                                .with("signature", signatureObject)
                                .end())
                        .with("scmBuildFrom", JsonUtils.`object`()
                                .with("buildView", JsonUtils.`object`()
                                        .with("build", JsonUtils.`object`()
                                                .with("id", 1)
                                                .with("name", "1")
                                                .with("description", "Build 1")
                                                .with("signature", signatureObject)
                                                .with("branch", JsonUtils.`object`()
                                                        .with("id", 10)
                                                        .with("name", "B")
                                                        .with("description", "Branch")
                                                        .with("disabled", false)
                                                        .with("signature", signatureObject)
                                                        .end())
                                                .end())
                                        .with("decorations", JsonUtils.array().end())
                                        .with("promotionRuns", JsonUtils.array().end())
                                        .with("validationStampRunViews", JsonUtils.array().end())
                                        .end())
                                .with("scm", JsonUtils.`object`()
                                        .with("placeholder", "")
                                        .end())
                                .end())
                        .with("scmBuildTo", JsonUtils.`object`()
                                .with("buildView", JsonUtils.`object`()
                                        .with("build", JsonUtils.`object`()
                                                .with("id", 2)
                                                .with("name", "2")
                                                .with("description", "Build 2")
                                                .with("signature", signatureObject)
                                                .with("branch", JsonUtils.`object`()
                                                        .with("id", 10)
                                                        .with("name", "B")
                                                        .with("description", "Branch")
                                                        .with("disabled", false)
                                                        .with("signature", signatureObject)
                                                        .end())
                                                .end())
                                        .with("decorations", JsonUtils.array().end())
                                        .with("promotionRuns", JsonUtils.array().end())
                                        .with("validationStampRunViews", JsonUtils.array().end())
                                        .end())
                                .with("scm", JsonUtils.`object`()
                                        .with("placeholder", "")
                                        .end())
                                .end())
                        .with("syncError", false)
                        .with("uuid", "uuid")
                        .with("_commits", "urn:test:net.nemerosa.ontrack.extension.git.GitController#changeLogCommits:uuid")
                        .with("_issues", "urn:test:net.nemerosa.ontrack.extension.git.GitController#changeLogIssues:uuid")
                        .with("_issuesIds", "urn:test:net.nemerosa.ontrack.extension.git.GitController#changeLogIssuesIds:uuid")
                        .with("_files", "urn:test:net.nemerosa.ontrack.extension.git.GitController#changeLogFiles:uuid")
                        .with("_changeLogFileFilters", "urn:test:net.nemerosa.ontrack.extension.scm.SCMController#getChangeLogFileFilters:1")
                        .with("_diff", "urn:test:net.nemerosa.ontrack.extension.git.GitController#diff:")
                        .with("_exportFormats", "urn:test:net.nemerosa.ontrack.extension.git.GitController#changeLogExportFormats:1")
                        .with("_exportIssues", "urn:test:net.nemerosa.ontrack.extension.git.GitController#changeLog:IssueChangeLogExportRequest%28format%3D%27text%27%2C+grouping%3D%27%27%2C+exclude%3D%27%27%2C+altGroup%3D%27Other%27%29")
                        .end(),
                changeLog
        )
    }

    companion object {

        private fun assertResourceJson(mapper: ResourceObjectMapper, expectedJson: JsonNode, o: Any) {
            assertEquals(
                    mapper.objectMapper.writeValueAsString(expectedJson),
                    mapper.write(o)
            )
        }
    }
}