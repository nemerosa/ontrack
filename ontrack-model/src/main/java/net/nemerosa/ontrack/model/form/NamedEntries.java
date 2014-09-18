package net.nemerosa.ontrack.model.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class NamedEntries extends AbstractField<NamedEntries> {

    private String nameLabel = "Name";
    private boolean nameRequired = true;
    private String addText = "Add an entry";

    public NamedEntries(String name) {
        super("namedEntries", name);
    }

    public static NamedEntries of(String name) {
        return new NamedEntries(name);
    }

    public NamedEntries nameLabel(String value) {
        nameLabel = value;
        return this;
    }

    public NamedEntries nameOptional() {
        this.nameRequired = false;
        return this;
    }

    public NamedEntries addText(String value) {
        this.addText = value;
        return this;
    }
}
