package net.nemerosa.ontrack.extension.svn.support;

import net.nemerosa.ontrack.extension.svn.model.*;
import net.nemerosa.ontrack.model.structure.*;
import org.junit.Test;

import static net.nemerosa.ontrack.extension.svn.support.SVNUtils.*;
import static org.junit.Assert.*;

public class SVNUtilsTest {

    @Test
    public void followsBuildPattern_tag_prefix_ok() {
        assertTrue(followsBuildPattern(new SVNLocation("/project/tags/2.7.0.1", 100000L), "/project/tags/{build:2.7.*}"));
    }

    @Test
    public void followsBuildPattern_tag_prefix_nok() {
        assertFalse(followsBuildPattern(new SVNLocation("/project/tags/2.6.0.1", 100000L), "/project/tags/{build:2.7.*}"));
    }

    @Test
    public void followsBuildPattern_tag_ok() {
        assertTrue(followsBuildPattern(new SVNLocation("/project/tags/2.7.0.1", 100000L), "/project/tags/{build}"));
    }

    @Test
    public void followsBuildPattern_tag_nok() {
        assertFalse(followsBuildPattern(new SVNLocation("/project/branches/2.7.x", 100000L), "/project/tags/{build}"));
    }

    @Test
    public void followsBuildPattern_revision_ok() {
        assertTrue(followsBuildPattern(new SVNLocation("/project/trunk", 100000L), "/project/trunk@{build}"));
    }

    @Test
    public void followsBuildPattern_revision_nok() {
        assertFalse(followsBuildPattern(new SVNLocation("/project/tags/2.7.0.1", 100000L), "/project/trunk@{build}"));
    }

    @Test
    public void getBuildName_tag_prefix() {
        assertEquals("2.7.0.1", getBuildName(new SVNLocation("/project/tags/2.7.0.1", 100000L), "/project/tags/{build:2.7.*}"));
    }

    @Test(expected = BuildPathMatchingException.class)
    public void getBuildName_tag_prefix_nok() {
        getBuildName(new SVNLocation("/project/tags/2.6.0.1", 100000L), "/project/tags/{build:2.7.*}");
    }

    @Test
    public void getBuildName_tag() {
        assertEquals("2.7.0.1", getBuildName(new SVNLocation("/project/tags/2.7.0.1", 100000L), "/project/tags/{build}"));
    }

    @Test
    public void getBuildName_revision() {
        assertEquals("123456", getBuildName(new SVNLocation("/project/trunk", 123456), "/project/trunk@{build}"));
    }

    @Test(expected = BuildPathMatchingException.class)
    public void getBuildName_revision_nok() {
        getBuildName(new SVNLocation("/project/branches/2.6.x", 123456), "/project/trunk@{build}");
    }

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

    @Test(expected = BuildPathNotDefinedException.class)
    public void getBasePath_null() {
        getBasePath(null);
    }

    @Test(expected = BuildPathNotDefinedException.class)
    public void getBasePath_blank() {
        getBasePath("");
    }

    @Test(expected = NoBasePathForRevisionPatternException.class)
    public void getBasePath_revision() {
        getBasePath("/project/trunk/@{build}");
    }

    @Test
    public void getBasePath_incorrect_pattern() {
        assertEquals("/project/tags", getBasePath("/project/tags/{test}"));
    }

    @Test
    public void getBasePath_simple() {
        assertEquals("/project/tags", getBasePath("/project/tags/{build}"));
    }

    @Test
    public void getBasePath_simple_with_prefix() {
        assertEquals("/project/tags", getBasePath("/project/tags/v{build}"));
    }

    @Test
    public void getBasePath_expression() {
        assertEquals("/project/tags", getBasePath("/project/tags/{build:2.7.*}"));
    }

    @Test
    public void getBasePath_expression_with_prefix() {
        assertEquals("/project/tags", getBasePath("/project/tags/v{build:2.7.*}"));
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
