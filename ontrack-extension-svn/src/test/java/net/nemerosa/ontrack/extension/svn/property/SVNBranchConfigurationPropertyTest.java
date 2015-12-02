package net.nemerosa.ontrack.extension.svn.property;

import net.nemerosa.ontrack.extension.svn.model.BranchPathRequiredException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SVNBranchConfigurationPropertyTest {

    @Test(expected = BranchPathRequiredException.class)
    public void branch_path_cannot_be_null() {
        new SVNBranchConfigurationProperty(null, null);
    }

    @Test(expected = BranchPathRequiredException.class)
    public void branch_path_cannot_be_blank() {
        new SVNBranchConfigurationProperty(null, null);
    }

    @Test
    public void cured_path_slash() {
        assertEquals("/", new SVNBranchConfigurationProperty(" / ", null).getCuredBranchPath());
    }

    @Test
    public void cured_path_trunk() {
        assertEquals("/trunk", new SVNBranchConfigurationProperty(" /trunk/ ", null).getCuredBranchPath());
    }

    @Test
    public void cured_path_project_trunk() {
        assertEquals("/project/trunk", new SVNBranchConfigurationProperty(" /project/trunk/ ", null).getCuredBranchPath());
    }

    @Test
    public void cured_path_branch() {
        assertEquals("/branches/xxx", new SVNBranchConfigurationProperty(" /branches/xxx/ ", null).getCuredBranchPath());
    }

    @Test
    public void cured_path_project_branch() {
        assertEquals("/project/branches/xxx", new SVNBranchConfigurationProperty(" /project/branches/xxx/ ", null).getCuredBranchPath());
    }

}
