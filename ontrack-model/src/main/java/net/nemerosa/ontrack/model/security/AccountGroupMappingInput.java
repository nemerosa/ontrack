package net.nemerosa.ontrack.model.security;

import lombok.Data;
import net.nemerosa.ontrack.model.structure.ID;

@Data
public class AccountGroupMappingInput {

    private final String name;
    private final ID group;

}
