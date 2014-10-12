package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.TemplateDefinition;

import java.util.Optional;

public interface BranchTemplateRepository {

    Optional<TemplateDefinition> getTemplateDefinition(ID branchId);

    void setTemplateDefinition(ID branchId, TemplateDefinition templateDefinition);

}
