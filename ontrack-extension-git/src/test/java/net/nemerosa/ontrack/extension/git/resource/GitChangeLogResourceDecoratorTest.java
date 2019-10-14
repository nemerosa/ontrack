package net.nemerosa.ontrack.extension.git.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.nemerosa.ontrack.extension.git.model.BasicGitActualConfiguration;
import net.nemerosa.ontrack.extension.git.model.BasicGitConfiguration;
import net.nemerosa.ontrack.extension.git.model.GitBuildInfo;
import net.nemerosa.ontrack.extension.git.model.GitChangeLog;
import net.nemerosa.ontrack.extension.git.service.GitService;
import net.nemerosa.ontrack.extension.issues.support.MockIssueServiceConfiguration;
import net.nemerosa.ontrack.extension.scm.model.SCMBuildView;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.ui.controller.MockURIBuilder;
import net.nemerosa.ontrack.ui.resource.DefaultResourceContext;
import net.nemerosa.ontrack.ui.resource.ResourceObjectMapper;
import net.nemerosa.ontrack.ui.resource.ResourceObjectMapperFactory;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.nemerosa.ontrack.json.JsonUtils.array;
import static net.nemerosa.ontrack.json.JsonUtils.object;
import static net.nemerosa.ontrack.model.structure.NameDescription.nd;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GitChangeLogResourceDecoratorTest {

    private ResourceObjectMapper mapper;
    private GitService gitService;

    @Before
    public void before() {
        SecurityService securityService = mock(SecurityService.class);
        gitService = mock(GitService.class);
        mapper = new ResourceObjectMapperFactory().resourceObjectMapper(
                new DefaultResourceContext(new MockURIBuilder(), securityService),
                new GitChangeLogResourceDecorator(gitService)
        );
    }

    @Test
    public void gitChangeLogWithIssues() throws JsonProcessingException {
        Signature signature = Signature.of(LocalDateTime.of(2014, 12, 5, 21, 53), "user");
        Project project = Project.of(nd("P", "Project")).withId(ID.of(1)).withSignature(signature);
        Branch branch = Branch.of(project, nd("B", "Branch")).withId(ID.of(10)).withSignature(signature);

        List<BuildView> buildView = Stream.of(1, 2)
                .map(it -> BuildView.of(
                        Build.of(
                                branch,
                                nd(String.valueOf(it), "Build " + it),
                                signature
                        ).withId(
                                ID.of(it)
                        )
                        )
                )
                .collect(Collectors.toList());

        GitChangeLog changeLog = new GitChangeLog(
                "uuid",
                project,
                new SCMBuildView<>(
                        buildView.get(0),
                        GitBuildInfo.INSTANCE
                ),
                new SCMBuildView<>(
                        buildView.get(1),
                        GitBuildInfo.INSTANCE
                ),
                false
        );

        when(gitService.getProjectConfiguration(project)).thenReturn(
                new BasicGitActualConfiguration(
                        BasicGitConfiguration.empty().withName("MyConfig").withIssueServiceConfigurationIdentifier("mock:MyTest"),
                        MockIssueServiceConfiguration.configuredIssueService("MyTest")
                )
        );

        ObjectNode signatureObject = object()
                .with("time", "2014-12-05T21:53:00Z")
                .with("user", object()
                        .with("name", "user")
                        .end())
                .end();

        assertResourceJson(
                mapper,
                object()
                        .with("project", object()
                                .with("id", 1)
                                .with("name", "P")
                                .with("description", "Project")
                                .with("disabled", false)
                                .with("signature", signatureObject)
                                .end())
                        .with("scmBuildFrom", object()
                                .with("buildView", object()
                                        .with("build", object()
                                                .with("id", 1)
                                                .with("name", "1")
                                                .with("description", "Build 1")
                                                .with("signature", signatureObject)
                                                .with("branch", object()
                                                        .with("id", 10)
                                                        .with("name", "B")
                                                        .with("description", "Branch")
                                                        .with("disabled", false)
                                                        .with("type", "CLASSIC")
                                                        .with("signature", signatureObject)
                                                        .end())
                                                .end())
                                        .with("decorations", array().end())
                                        .with("promotionRuns", array().end())
                                        .with("validationStampRunViews", array().end())
                                        .end())
                                .with("scm", object()
                                        .with("placeholder", "")
                                        .end())
                                .end())
                        .with("scmBuildTo", object()
                                .with("buildView", object()
                                        .with("build", object()
                                                .with("id", 2)
                                                .with("name", "2")
                                                .with("description", "Build 2")
                                                .with("signature", signatureObject)
                                                .with("branch", object()
                                                        .with("id", 10)
                                                        .with("name", "B")
                                                        .with("description", "Branch")
                                                        .with("disabled", false)
                                                        .with("type", "CLASSIC")
                                                        .with("signature", signatureObject)
                                                        .end())
                                                .end())
                                        .with("decorations", array().end())
                                        .with("promotionRuns", array().end())
                                        .with("validationStampRunViews", array().end())
                                        .end())
                                .with("scm", object()
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
                        .with("_page", "urn:test:#:extension/git/changelog?from=1&to=2")
                        .end(),
                changeLog
        );
    }

    @Test
    public void gitChangeLogWithoutIssues() throws JsonProcessingException {

        Signature signature = Signature.of(LocalDateTime.of(2014, 12, 5, 21, 53), "user");

        Project project = Project.of(nd("P", "Project")).withId(ID.of(1)).withSignature(signature);
        Branch branch = Branch.of(project, nd("B", "Branch")).withId(ID.of(10)).withSignature(signature);

        List<BuildView> buildView = Stream.of(1, 2)
                .map(it -> BuildView.of(
                        Build.of(
                                branch,
                                nd(String.valueOf(it), "Build " + it),
                                signature
                        ).withId(ID.of(it))
                        )
                )
                .collect(Collectors.toList());

        GitChangeLog changeLog = new GitChangeLog(
                "uuid",
                project,
                new SCMBuildView<>(
                        buildView.get(0),
                        GitBuildInfo.INSTANCE
                ),
                new SCMBuildView<>(
                        buildView.get(1),
                        GitBuildInfo.INSTANCE
                ),
                false
        );

        when(gitService.getProjectConfiguration(project)).thenReturn(
                new BasicGitActualConfiguration(
                        BasicGitConfiguration.empty().withName("MyConfig").withIssueServiceConfigurationIdentifier("mock:MyTest"),
                        null
                )
        );

        ObjectNode signatureObject = object()
                .with("time", "2014-12-05T21:53:00Z")
                .with("user", object()
                        .with("name", "user")
                        .end())
                .end();

        assertResourceJson(
                mapper,
                object()
                        .with("project", object()
                                .with("id", 1)
                                .with("name", "P")
                                .with("description", "Project")
                                .with("disabled", false)
                                .with("signature", signatureObject)
                                .end())
                        .with("scmBuildFrom", object()
                                .with("buildView", object()
                                        .with("build", object()
                                                .with("id", 1)
                                                .with("name", "1")
                                                .with("description", "Build 1")
                                                .with("signature", signatureObject)
                                                .with("branch", object()
                                                        .with("id", 10)
                                                        .with("name", "B")
                                                        .with("description", "Branch")
                                                        .with("disabled", false)
                                                        .with("type", "CLASSIC")
                                                        .with("signature", signatureObject)
                                                        .end())
                                                .end())
                                        .with("decorations", array().end())
                                        .with("promotionRuns", array().end())
                                        .with("validationStampRunViews", array().end())
                                        .end())
                                .with("scm", object()
                                        .with("placeholder", "")
                                        .end())
                                .end())
                        .with("scmBuildTo", object()
                                .with("buildView", object()
                                        .with("build", object()
                                                .with("id", 2)
                                                .with("name", "2")
                                                .with("description", "Build 2")
                                                .with("signature", signatureObject)
                                                .with("branch", object()
                                                        .with("id", 10)
                                                        .with("name", "B")
                                                        .with("description", "Branch")
                                                        .with("disabled", false)
                                                        .with("type", "CLASSIC")
                                                        .with("signature", signatureObject)
                                                        .end())
                                                .end())
                                        .with("decorations", array().end())
                                        .with("promotionRuns", array().end())
                                        .with("validationStampRunViews", array().end())
                                        .end())
                                .with("scm", object()
                                        .with("placeholder", "")
                                        .end())
                                .end())
                        .with("syncError", false)
                        .with("uuid", "uuid")
                        .with("_commits", "urn:test:net.nemerosa.ontrack.extension.git.GitController#changeLogCommits:uuid")
                        .with("_files", "urn:test:net.nemerosa.ontrack.extension.git.GitController#changeLogFiles:uuid")
                        .with("_changeLogFileFilters", "urn:test:net.nemerosa.ontrack.extension.scm.SCMController#getChangeLogFileFilters:1")
                        .with("_diff", "urn:test:net.nemerosa.ontrack.extension.git.GitController#diff:")
                        .with("_exportFormats", "urn:test:net.nemerosa.ontrack.extension.git.GitController#changeLogExportFormats:1")
                        .with("_exportIssues", "urn:test:net.nemerosa.ontrack.extension.git.GitController#changeLog:IssueChangeLogExportRequest%28format%3D%27text%27%2C+grouping%3D%27%27%2C+exclude%3D%27%27%2C+altGroup%3D%27Other%27%29")
                        .with("_page", "urn:test:#:extension/git/changelog?from=1&to=2")
                        .end(),
                changeLog
        );
    }

    private static void assertResourceJson(ResourceObjectMapper mapper, JsonNode expectedJson, Object o) throws JsonProcessingException {
        assertEquals(
                mapper.getObjectMapper().writeValueAsString(expectedJson),
                mapper.write(o)
        );
    }

}