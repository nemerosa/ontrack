package net.nemerosa.ontrack.extension.svn.support;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import static net.nemerosa.ontrack.json.JsonUtils.object;
import static org.junit.Assert.assertEquals;

public class BuildSvnRevisionLinkMigrationActionTest {

    @Test
    public void nodeMigration() {
        BuildSvnRevisionLinkMigrationAction action = new BuildSvnRevisionLinkMigrationAction();
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

}
