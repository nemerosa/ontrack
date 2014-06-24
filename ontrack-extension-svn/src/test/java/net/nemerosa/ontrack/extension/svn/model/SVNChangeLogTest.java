package net.nemerosa.ontrack.extension.svn.model;

import net.nemerosa.ontrack.extension.scm.changelog.SCMBuildView;
import net.nemerosa.ontrack.extension.svn.db.SVNRepository;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.model.support.Time;
import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

import static net.nemerosa.ontrack.extension.svn.model.SVNTestFixtures.configuration;
import static org.junit.Assert.*;

public class SVNChangeLogTest {

    @Test
    public void none() {
        Branch branch = branch();
        SVNRepository repository = repository();

        SCMBuildView<SVNHistory> scmBuildFrom = new SCMBuildView<>(
                BuildView.of(
                        Build.of(
                                branch, new NameDescription("10", "Build 10"), Signature.of("user")
                        )
                ),
                new SVNHistory(
                        new SVNReference("/project/trunk", "http://server/project/trunk", 100000, Time.now())
                )
        );

        SVNChangeLog changeLog = new SVNChangeLog(
                UUID.randomUUID().toString(),
                branch,
                repository,
                scmBuildFrom,
                scmBuildFrom
        );

        // Gets the references
        Collection<SVNChangeLogReference> references = changeLog.getChangeLogReferences();

        // Check
        assertNotNull(references);
        assertEquals(1, references.size());
        assertTrue(references.iterator().next().isNone());
    }

    private static SVNRepository repository() {
        return SVNRepository.of(
                1,
                configuration(),
                null
        );
    }

    private static Branch branch() {
        Project project = Project.of(new NameDescription("P", "Project")).withId(ID.of(1));
        return Branch.of(project, new NameDescription("B", "Branch")).withId(ID.of(1));
    }

    @Test
    public void simple() {
        Branch branch = branch();
        SVNRepository repository = repository();

        SCMBuildView<SVNHistory> scmBuildFrom = new SCMBuildView<>(
                BuildView.of(
                        Build.of(
                                branch, new NameDescription("10", "Build 10"), Signature.of("user")
                        )
                ),
                new SVNHistory(
                        new SVNReference("/project/trunk", "http://server/project/trunk", 110000, Time.now())
                )
        );

        SCMBuildView<SVNHistory> scmBuildTo = new SCMBuildView<>(
                BuildView.of(
                        Build.of(
                                branch, new NameDescription("20", "Build 20"), Signature.of("user")
                        )
                ),
                new SVNHistory(
                        new SVNReference("/project/trunk", "http://server/project/trunk", 120000, Time.now())
                )
        );

        SVNChangeLog changeLog = new SVNChangeLog(
                UUID.randomUUID().toString(),
                branch,
                repository,
                scmBuildFrom,
                scmBuildTo
        );

        // Gets the references
        Collection<SVNChangeLogReference> references = changeLog.getChangeLogReferences();

        // Check
        assertNotNull(references);
        assertEquals(1, references.size());
        SVNChangeLogReference reference = references.iterator().next();
        assertEquals("/project/trunk", reference.getPath());
        assertEquals(110000, reference.getStart());
        assertEquals(120000, reference.getEnd());
    }

    @Test
    public void branch_low() {
        Branch branch = branch();
        SVNRepository repository = repository();

        SCMBuildView<SVNHistory> scmBuildFrom = new SCMBuildView<>(
                BuildView.of(
                        Build.of(
                                branch, new NameDescription("10", "Build 10"), Signature.of("user")
                        )
                ),
                new SVNHistory(
                        new SVNReference("/project/branches/10", "http://server/project/branches/10", 110000, Time.now()),
                        new SVNReference("/project/trunk", "http://server/project/trunk", 100000, Time.now())
                )
        );

        SCMBuildView<SVNHistory> scmBuildTo = new SCMBuildView<>(
                BuildView.of(
                        Build.of(
                                branch, new NameDescription("5", "Build 5"), Signature.of("user")
                        )
                ),
                new SVNHistory(
                        new SVNReference("/project/trunk", "http://server/project/trunk", 90000, Time.now())
                )
        );

        SVNChangeLog changeLog = new SVNChangeLog(
                UUID.randomUUID().toString(),
                branch,
                repository,
                scmBuildFrom,
                scmBuildTo
        );

        // Gets the references
        Collection<SVNChangeLogReference> references = changeLog.getChangeLogReferences();

        // Check
        assertNotNull(references);
        assertEquals(2, references.size());
        Iterator<SVNChangeLogReference> iterator = references.iterator();
        SVNChangeLogReference referenceBranch = iterator.next();
        {
            assertEquals("/project/branches/10", referenceBranch.getPath());
            assertEquals(0, referenceBranch.getStart());
            assertEquals(110000, referenceBranch.getEnd());
        }
        SVNChangeLogReference referenceTrunk = iterator.next();
        {
            assertEquals("/project/trunk", referenceTrunk.getPath());
            assertEquals(90000, referenceTrunk.getStart());
            assertEquals(100000, referenceTrunk.getEnd());
        }
    }
}
