package net.nemerosa.ontrack.extension.svn.support;

import net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationProperty;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.Signature;
import net.nemerosa.ontrack.model.support.NoConfig;
import org.junit.Test;

import java.util.OptionalLong;

import static net.nemerosa.ontrack.model.structure.NameDescription.nd;
import static org.junit.Assert.*;

public class RevisionSvnRevisionLinkTest {

    private final RevisionSvnRevisionLink link = new RevisionSvnRevisionLink();
    private SVNBranchConfigurationProperty branchConfigurationProperty = new SVNBranchConfigurationProperty(
            "/trunk",
            new ConfiguredBuildSvnRevisionLink<>(
                    new RevisionSvnRevisionLink(),
                    NoConfig.INSTANCE
            ).toServiceConfiguration(),
            ""
    );

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

    @Test
    public void getRevision_numeric() {
        assertEquals(
                OptionalLong.of(1234L),
                link.getRevision(
                        NoConfig.INSTANCE,
                        Build.of(
                                Branch.of(
                                        Project.of(nd("P", "")),
                                        nd("B", "")
                                ),
                                nd("1234", ""),
                                Signature.of("test")
                        ),
                        branchConfigurationProperty
                )
        );
    }

    @Test
    public void getRevision_not_numeric() {
        assertEquals(
                OptionalLong.empty(),
                link.getRevision(
                        NoConfig.INSTANCE,
                        Build.of(
                                Branch.of(
                                        Project.of(nd("P", "")),
                                        nd("B", "")
                                ),
                                nd("1.0-1234", ""),
                                Signature.of("test")
                        ),
                        branchConfigurationProperty
                )
        );
    }

}
