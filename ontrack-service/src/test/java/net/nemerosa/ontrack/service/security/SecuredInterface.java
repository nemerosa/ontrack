package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.model.annotations.GlobalGrant;
import net.nemerosa.ontrack.model.annotations.ProjectCreation;

public interface SecuredInterface {

    @GlobalGrant(ProjectCreation.class)
    void global_grant();

}
