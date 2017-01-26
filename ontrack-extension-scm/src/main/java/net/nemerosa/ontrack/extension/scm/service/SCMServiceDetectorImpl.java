package net.nemerosa.ontrack.extension.scm.service;

import net.nemerosa.ontrack.model.structure.Branch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SCMServiceDetectorImpl implements SCMServiceDetector {

    private final List<SCMServiceProvider> scmServiceProviders;

    @Autowired
    public SCMServiceDetectorImpl(List<SCMServiceProvider> scmServiceProviders) {
        this.scmServiceProviders = scmServiceProviders;
    }

    @Override
    public Optional<SCMService> getScmService(Branch branch) {
        return scmServiceProviders.stream()
                .map(scmServiceProvider -> scmServiceProvider.getScmService(branch))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }
}
