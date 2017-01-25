package net.nemerosa.ontrack.extension.scm.service;

import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.model.structure.Project;
import org.junit.Test;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SCMServiceDetectorImplTest {

    @Test
    public void no_result_when_no_provider() {
        SCMServiceDetectorImpl detector = new SCMServiceDetectorImpl(Collections.emptyList());
        Optional<SCMService> scmService = detector.getScmService(Branch.of(Project.of(NameDescription.nd("P", "")), NameDescription.nd("B", "")));
        assertNotNull(scmService);
        assertFalse(scmService.isPresent());
    }

    @Test
    public void no_result_when_nop_provider() {
        SCMServiceDetectorImpl detector = new SCMServiceDetectorImpl(Collections.singletonList(new NOPSCMServiceProvider()));
        Optional<SCMService> scmService = detector.getScmService(Branch.of(Project.of(NameDescription.nd("P", "")), NameDescription.nd("B", "")));
        assertNotNull(scmService);
        assertFalse(scmService.isPresent());
    }

    @Test
    public void provider() {
        Branch branch = Branch.of(Project.of(NameDescription.nd("P", "")), NameDescription.nd("B", ""));
        SCMService service = mock(SCMService.class);
        SCMServiceProvider provider = mock(SCMServiceProvider.class);
        when(provider.getScmService(branch)).thenReturn(Optional.of(service));
        SCMServiceDetectorImpl detector = new SCMServiceDetectorImpl(Collections.singletonList(provider));
        Optional<SCMService> scmService = detector.getScmService(branch);
        assertNotNull(scmService);
        assertTrue(scmService.isPresent());
        assertSame(service, scmService.orElse(null));
    }

}
