package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.TemplateDefinition;
import net.nemerosa.ontrack.model.structure.TemplateInstance;

import java.util.Optional;

public interface BranchTemplateRepository {

    Optional<TemplateDefinition> getTemplateDefinition(ID branchId);

    void setTemplateDefinition(ID branchId, TemplateDefinition templateDefinition);

    boolean isTemplateDefinition(ID branchId);

    Optional<TemplateInstance> getTemplateInstance(ID branchId);

    void setTemplateInstance(ID branchId, TemplateInstance templateInstance);

    boolean isTemplateInstance(ID branchId);
}
