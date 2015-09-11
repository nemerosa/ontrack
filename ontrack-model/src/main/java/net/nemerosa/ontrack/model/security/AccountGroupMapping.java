package net.nemerosa.ontrack.model.security;

import lombok.Data;
import net.nemerosa.ontrack.model.structure.Entity;
import net.nemerosa.ontrack.model.structure.ID;

@Data
public class AccountGroupMapping implements Entity {

    private final ID id;
    private final String name;
    private final AccountGroup group;

}
