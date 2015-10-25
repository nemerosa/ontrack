package net.nemerosa.ontrack.extension.svn.support;

import net.nemerosa.ontrack.extension.svn.model.BuildPathMatchingException;
import net.nemerosa.ontrack.extension.svn.model.UnknownBuildPathExpression;
import net.nemerosa.ontrack.model.structure.*;
import org.junit.Test;

import static net.nemerosa.ontrack.extension.svn.support.SVNUtils.*;
import static org.junit.Assert.*;

public class SVNUtilsTest {

    @Test
    public void expandBuildPath_build() {
        assertEquals("/project/tags/v1", expandBuildPath("/project/tags/v{build}", build()));
    }

    @Test
    public void expandBuildPath_build_with_regex() {
        assertEquals("/project/tags/2.1.8", expandBuildPath("/project/tags/{build:2.1.*}", build("2.1.8")));
    }

    @Test(expected = BuildPathMatchingException.class)
    public void expandBuildPath_build_with_regex_no_match() {
        assertEquals("/project/tags/2.1.8", expandBuildPath("/project/tags/{build:2.2.*}", build("2.1.8")));
    }

    @Test(expected = UnknownBuildPathExpression.class)
    public void expandBuildPath_unknown() {
        expandBuildPath("/project/tags/v{test}", build());
    }

    @Test
    public void expandBuildPathExpression_build() {
        assertEquals("1", expandBuildPathExpression("build", "1"));
    }

    @Test(expected = UnknownBuildPathExpression.class)
    public void expandBuildPathExpression_unknown() {
        expandBuildPathExpression("test", "1");
    }

    @Test
    public void buildPattern_match() {
        assertTrue(buildPatternOk("2.1.*", "2.1.8.0"));
    }

    @Test
    public void buildPattern_match_nok() {
        assertFalse(buildPatternOk("2.1.*", "2.2.0"));
    }

    private static Build build() {
        return build("1");
    }

    private static Build build(String name) {
        Project p = Project.of(new NameDescription("P", "Project")).withId(ID.of(1));
        Branch b = Branch.of(p, new NameDescription("B", "Branch")).withId(ID.of(1));
        return Build.of(b, new NameDescription(name, "Build"), Signature.of("user")).withId(ID.of(1));
    }

}
