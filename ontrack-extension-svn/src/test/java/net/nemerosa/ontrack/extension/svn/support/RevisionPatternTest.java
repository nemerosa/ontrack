package net.nemerosa.ontrack.extension.svn.support;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class RevisionPatternTest {

    @Test
    public void constructor_ok() {
        assertEquals("11.8.4.*-{revision}", new RevisionPattern("11.8.4.*-{revision}").getPattern());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_nok() {
        new RevisionPattern("11.8.4.*-{revision }");
    }

    @Test
    public void validity() {
        RevisionPattern r = new RevisionPattern("11.8.4.*-{revision}");
        assertFalse(r.isValidBuildName("11.8.5.0-123456"));
        assertFalse(r.isValidBuildName("11.8.4-123456"));
        assertTrue(r.isValidBuildName("11.8.4.5-123456"));
        assertFalse(r.isValidBuildName("11.8.4.5-v123456"));
    }

    @Test
    public void extract_revision() {
        RevisionPattern r = new RevisionPattern("11.8.4.*-{revision}");
        assertFalse(r.extractRevision("11.8.5.0-123456").isPresent());
        assertFalse(r.extractRevision("11.8.4-123456").isPresent());
        assertEquals(123456L, r.extractRevision("11.8.4.5-123456").getAsLong());
        assertFalse(r.extractRevision("11.8.4.5-v123456").isPresent());
    }

    @Test
    public void clone_no_replacement() {
        RevisionPattern r = new RevisionPattern("11.8.4*-{revision}");
        assertEquals("11.8.4*-{revision}", r.clone(value -> StringUtils.replace(value, "${sourceName}", "1.0")).getPattern());
    }

    @Test
    public void clone_replacement() {
        RevisionPattern r = new RevisionPattern("${sourceName}*-{revision}");
        assertEquals("1.0*-{revision}", r.clone(value -> StringUtils.replace(value, "${sourceName}", "1.0")).getPattern());
    }

}
