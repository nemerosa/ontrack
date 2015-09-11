package net.nemerosa.ontrack.model.security;

import lombok.Data;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Selection;
import net.nemerosa.ontrack.model.structure.Entity;
import net.nemerosa.ontrack.model.structure.ID;

import java.util.List;

@Data
public class AccountGroupMapping implements Entity {

    private final ID id;
    private final String type;
    private final String name;
    private final AccountGroup group;

    public static Form form(List<AccountGroup> groups) {
        return Form.create()
                .name()
                .with(
                        Selection.of("group")
                                .label("Group")
                                .items(groups)
                )
                ;
    }
}
