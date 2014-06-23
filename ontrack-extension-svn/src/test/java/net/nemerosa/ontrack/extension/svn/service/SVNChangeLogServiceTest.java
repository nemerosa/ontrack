package net.nemerosa.ontrack.extension.svn.service;

import net.nemerosa.ontrack.extension.svn.model.UnknownBuildPathExpression;
import net.nemerosa.ontrack.extension.svn.service.SVNChangeLogServiceImpl;
import net.nemerosa.ontrack.model.structure.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SVNChangeLogServiceTest {

    @Test
    public void expandBuildPath_build() {
        SVNChangeLogServiceImpl service = emptyService();
        assertEquals("/project/tags/v1", service.expandBuildPath("/project/tags/v{build}", build()));
    }

    @Test(expected = UnknownBuildPathExpression.class)
    public void expandBuildPath_unknown() {
        SVNChangeLogServiceImpl service = emptyService();
        service.expandBuildPath("/project/tags/v{test}", build());
    }

    @Test
    public void expandBuildPathExpression_build() {
        SVNChangeLogServiceImpl service = emptyService();
        assertEquals("1", service.expandBuildPathExpression("build", build()));
    }

    @Test(expected = UnknownBuildPathExpression.class)
    public void expandBuildPathExpression_unknown() {
        SVNChangeLogServiceImpl service = emptyService();
        service.expandBuildPathExpression("test", build());
    }

    private static Build build() {
        Project p = Project.of(new NameDescription("P", "Project")).withId(ID.of(1));
        Branch b = Branch.of(p, new NameDescription("B", "Branch")).withId(ID.of(1));
        return Build.of(b, new NameDescription("1", "Build"), Signature.of("user")).withId(ID.of(1));
    }

    private SVNChangeLogServiceImpl emptyService() {
        return new SVNChangeLogServiceImpl(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
    }

}
