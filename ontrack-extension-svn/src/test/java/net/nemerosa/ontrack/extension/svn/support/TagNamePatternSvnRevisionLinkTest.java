package net.nemerosa.ontrack.extension.svn.support;

import net.nemerosa.ontrack.extension.scm.support.TagPattern;
import net.nemerosa.ontrack.extension.svn.service.SVNService;
import net.nemerosa.ontrack.model.structure.StructureService;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class TagNamePatternSvnRevisionLinkTest {

    private SVNService svnService;
    private StructureService structureService;
    private TagNamePatternSvnRevisionLink link;

    @Before
    public void before() {
        svnService = mock(SVNService.class);
        structureService = mock(StructureService.class);
        link = new TagNamePatternSvnRevisionLink(svnService, structureService);
    }

    @Test
    public void getBuildNameFromValidTag() {
        Optional<String> o = link.getBuildName(new TagPattern("11.8.4.*"), "11.8.4.0-5");
        assertTrue(o.isPresent());
        assertEquals("11.8.4.0-5", o.get());
    }

    @Test
    public void getBuildNameFromInvalidTag() {
        Optional<String> o = link.getBuildName(new TagPattern("11.8.4.*"), "11.8.5.0-10");
        assertFalse(o.isPresent());
    }

    @Test
    public void getTagNameFromValidBuildName() {
        Optional<String> o = link.getTagName(new TagPattern("11.8.4.*"), "11.8.4.0-5");
        assertTrue(o.isPresent());
        assertEquals("11.8.4.0-5", o.get());
    }

    @Test
    public void getTagNameFromInvalidBuildName() {
        Optional<String> o = link.getTagName(new TagPattern("11.8.4.*"), "11.8.5.0-5");
        assertFalse(o.isPresent());
    }

}
