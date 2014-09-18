package net.nemerosa.ontrack.model.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class NamedEntry extends AbstractField<NamedEntry> {

    private String nameLabel = "Name";
    private boolean nameRequired = true;

    public NamedEntry(String name) {
        super("namedEntry", name);
    }

    public static NamedEntry of(String name) {
        return new NamedEntry(name);
    }

    public NamedEntry nameLabel(String value) {
        nameLabel = value;
        return this;
    }

    public NamedEntry nameOptional() {
        this.nameRequired = false;
        return this;
    }
}
