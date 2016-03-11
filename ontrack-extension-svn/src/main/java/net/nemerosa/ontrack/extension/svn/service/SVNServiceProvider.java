package net.nemerosa.ontrack.extension.svn.service;

import net.nemerosa.ontrack.extension.scm.service.SCMService;
import net.nemerosa.ontrack.extension.scm.service.SCMServiceProvider;
import net.nemerosa.ontrack.model.structure.Branch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SVNServiceProvider implements SCMServiceProvider {

    private final SVNService svnService;

    @Autowired
    public SVNServiceProvider(SVNService svnService) {
        this.svnService = svnService;
    }

    @Override
    public Optional<SCMService> getScmService(Branch branch) {
        return svnService.getSVNRepository(branch).map(r -> svnService);
    }
}
