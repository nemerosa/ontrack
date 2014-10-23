package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.structure.*;

import java.util.Collection;
import java.util.Optional;

public interface BranchTemplateRepository {

    Collection<BranchTemplateDefinition> getTemplateDefinitions();

    Optional<TemplateDefinition> getTemplateDefinition(ID branchId);

    void setTemplateDefinition(ID branchId, TemplateDefinition templateDefinition);

    boolean isTemplateDefinition(ID branchId);

    Optional<TemplateInstance> getTemplateInstance(ID branchId);

    void setTemplateInstance(ID branchId, TemplateInstance templateInstance);

    boolean isTemplateInstance(ID branchId);

    Collection<BranchTemplateInstance> getTemplateInstancesForDefinition(ID templateDefinitionId);

    void disconnectTemplateInstance(ID branchId);
}
