package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.model.security.RoleContributor;
import org.springframework.stereotype.Component;

/**
 * Default NOP instance for injection
 */
@Component
public class NOPRoleContributor implements RoleContributor {
}
