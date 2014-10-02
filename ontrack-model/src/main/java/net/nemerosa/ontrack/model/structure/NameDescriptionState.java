package net.nemerosa.ontrack.model.structure;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.beans.ConstructorProperties;

@EqualsAndHashCode(callSuper = false)
@Data
public class NameDescriptionState extends NameDescription {

    private final boolean disabled;

    @ConstructorProperties({"name", "description", "disabled"})
    public NameDescriptionState(String name, String description, boolean disabled) {
        super(name, description);
        this.disabled = disabled;
    }

}
