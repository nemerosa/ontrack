package net.nemerosa.ontrack.extension.svn.support;

import com.fasterxml.jackson.databind.node.ObjectNode;
import net.nemerosa.ontrack.extension.scm.support.TagPattern;
import net.nemerosa.ontrack.model.support.NoConfig;
import org.junit.Before;
import org.junit.Test;

import static net.nemerosa.ontrack.json.JsonUtils.object;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class BuildSvnRevisionLinkMigrationActionTest {

    private RevisionSvnRevisionLink revisionLink = mock(RevisionSvnRevisionLink.class);
    private TagNamePatternSvnRevisionLink tagPatternLink = mock(TagNamePatternSvnRevisionLink.class);
    private TagNameSvnRevisionLink tagLink = mock(TagNameSvnRevisionLink.class);
    private BuildSvnRevisionLinkMigrationAction action;

    @Before
    public void before() {
        action = new BuildSvnRevisionLinkMigrationAction(revisionLink, tagPatternLink, tagLink);
    }

    @Test
    public void nodeMigration() {
        // Old node
        ObjectNode node = object()
                .with("branchPath", "/project/branches/1.1")
                .with("buildPath", "/project/tags/{build}")
                .end();
        // Migration
        action.migrate(node);
        // Checks the node
        assertEquals(
                object()
                        .with("branchPath", "/project/branches/1.1")
                        .with("buildRevisionLink", object()
                                        .with("id", "tag")
                                        .with("data", object().end())
                                        .end()
                        )
                        .end(),
                node
        );
    }

    @Test
    public void tagName() {
        ConfiguredBuildSvnRevisionLink<?> c = action.toBuildSvnRevisionLinkConfiguration(
                "/project/branches/1.1",
                "/project/tags/{build}"
        );
        assertTrue(c.getLink() instanceof TagNameSvnRevisionLink);
        assertTrue(c.getData() instanceof NoConfig);
    }

    @Test
    public void tagPatternName() {
        ConfiguredBuildSvnRevisionLink<?> c = action.toBuildSvnRevisionLinkConfiguration(
                "/project/branches/1.1",
                "/project/tags/{build:1.1.*}"
        );
        assertTrue(c.getLink() instanceof TagNamePatternSvnRevisionLink);
        assertTrue(c.getData() instanceof TagPattern);
        TagPattern tagPattern = (TagPattern) c.getData();
        assertEquals("1.1.*", tagPattern.getPattern());
    }

    @Test
    public void revision() {
        ConfiguredBuildSvnRevisionLink<?> c = action.toBuildSvnRevisionLinkConfiguration(
                "/project/branches/1.1",
                "/project/branches/1.1@{build}"
        );
        assertTrue(c.getLink() instanceof RevisionSvnRevisionLink);
        assertTrue(c.getData() instanceof NoConfig);
    }

}
