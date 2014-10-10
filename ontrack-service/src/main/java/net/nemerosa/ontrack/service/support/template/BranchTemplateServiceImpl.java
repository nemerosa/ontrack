package net.nemerosa.ontrack.service.support.template;

import net.nemerosa.ontrack.model.structure.BranchTemplateService;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.TemplateDefinition;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BranchTemplateServiceImpl implements BranchTemplateService {
    @Override
    public Optional<TemplateDefinition> getTemplateDefinition(ID branchId) {
        // FIXME Method net.nemerosa.ontrack.service.support.template.BranchTemplateServiceImpl.getTemplateDefinition
        return Optional.empty();
    }
}
