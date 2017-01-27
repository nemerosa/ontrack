package net.nemerosa.ontrack.extension.scm.service;

import net.nemerosa.ontrack.model.structure.Branch;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * PLaceholder for the SCM service providers.
 *
 * @see SCMServiceDetectorImpl
 */
@Component
public class NOPSCMServiceProvider implements SCMServiceProvider {
    @Override
    public Optional<SCMService> getScmService(Branch branch) {
        return Optional.empty();
    }
}
