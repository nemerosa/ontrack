package net.nemerosa.ontrack.extension.ldap;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.nemerosa.ontrack.model.security.AccountGroup;
import net.nemerosa.ontrack.model.security.AccountGroupMapping;
import net.nemerosa.ontrack.model.structure.ID;

import java.beans.ConstructorProperties;

@EqualsAndHashCode(callSuper = false)
@Data
public class LDAPMapping extends AccountGroupMapping {

    @ConstructorProperties({"id", "type", "name", "group"})
    public LDAPMapping(ID id, String type, String name, AccountGroup group) {
        super(id, type, name, group);
    }

    public static LDAPMapping of(AccountGroupMapping mapping) {
        return new LDAPMapping(
                mapping.getId(),
                mapping.getType(),
                mapping.getName(),
                mapping.getGroup()
        );
    }

}
