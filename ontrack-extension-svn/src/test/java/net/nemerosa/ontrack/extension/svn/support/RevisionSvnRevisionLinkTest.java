package net.nemerosa.ontrack.extension.svn.support;

import net.nemerosa.ontrack.model.support.NoConfig;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RevisionSvnRevisionLinkTest {

    private final RevisionSvnRevisionLink link = new RevisionSvnRevisionLink();

    @Test
    public void isValidBuildName_ok() {
        assertTrue(link.isValidBuildName(NoConfig.INSTANCE, "1"));
        assertTrue(link.isValidBuildName(NoConfig.INSTANCE, "100000"));
    }

    @Test
    public void isValidBuildName_zero_nok() {
        assertFalse(link.isValidBuildName(NoConfig.INSTANCE, "0"));
    }

    @Test
    public void isValidBuildName_minus_nok() {
        assertFalse(link.isValidBuildName(NoConfig.INSTANCE, "-1"));
        assertFalse(link.isValidBuildName(NoConfig.INSTANCE, "-10"));
    }

    @Test
    public void isValidBuildName_alpha_nok() {
        assertFalse(link.isValidBuildName(NoConfig.INSTANCE, "a"));
        assertFalse(link.isValidBuildName(NoConfig.INSTANCE, "10a"));
        assertFalse(link.isValidBuildName(NoConfig.INSTANCE, "a10"));
    }

}
