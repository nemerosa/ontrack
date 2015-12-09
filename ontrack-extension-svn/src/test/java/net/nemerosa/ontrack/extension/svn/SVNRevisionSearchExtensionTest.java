package net.nemerosa.ontrack.extension.svn;

import net.nemerosa.ontrack.extension.scm.SCMExtensionFeature;
import net.nemerosa.ontrack.extension.svn.service.SVNConfigurationService;
import net.nemerosa.ontrack.extension.svn.service.SVNService;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class SVNRevisionSearchExtensionTest {

    private final SVNRevisionSearchExtension extension = new SVNRevisionSearchExtension(
            new SVNExtensionFeature(new SCMExtensionFeature()),
            mock(URIBuilder.class),
            mock(SVNConfigurationService.class),
            mock(SVNService.class)
    );

    @Test
    public void numeric_search_only() {
        assertFalse(extension.getSearchProvider().isTokenSearchable("120 000"));
    }

    @Test
    public void numeric() {
        assertFalse(extension.getSearchProvider().isTokenSearchable("0"));
        assertTrue(extension.getSearchProvider().isTokenSearchable("1"));
        assertTrue(extension.getSearchProvider().isTokenSearchable("120000"));
        assertTrue(extension.getSearchProvider().isTokenSearchable("2147483646"));
        assertFalse(extension.getSearchProvider().isTokenSearchable("2147483647"));
        assertFalse(extension.getSearchProvider().isTokenSearchable("2147483648")); // Integer.MAXINT
    }

}
