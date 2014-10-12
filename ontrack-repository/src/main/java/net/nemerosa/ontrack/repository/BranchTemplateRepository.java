package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.TemplateDefinition;

public interface BranchTemplateRepository {

    void setTemplateDefinition(ID branchId, TemplateDefinition templateDefinition);

}
