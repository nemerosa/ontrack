package net.nemerosa.ontrack.model.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Multiple list of name/value fields. Suitable to deal with a list of
 * {@link net.nemerosa.ontrack.model.support.NameValue} items.
 *
 * @see net.nemerosa.ontrack.model.support.NameValue
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NamedEntries extends AbstractField<NamedEntries> {

    private String nameLabel = "Name";
    private String valueLabel = "Value";
    private boolean nameRequired = true;
    private String addText = "Add an entry";

    /**
     * Creates a form field for the <code>name</code> field.
     *
     * @param name Name of the field.
     */
    public NamedEntries(String name) {
        super("namedEntries", name);
    }

    /**
     * Creates a form field for the <code>name</code> field.
     *
     * @param name Name of the field.
     * @return Field
     */
    public static NamedEntries of(String name) {
        return new NamedEntries(name);
    }

    /**
     * Sets the label for the "name" input part of an entry.
     *
     * @param value Label used as a placeholder for the "name" input part of an entry.
     * @return Field to create
     */
    public NamedEntries nameLabel(String value) {
        nameLabel = value;
        return this;
    }

    /**
     * Sets the label for the "value" input part of an entry.
     *
     * @param value Label used as a placeholder for the "value" input part of an entry.
     * @return Field to create
     */
    public NamedEntries valueLabel(String value) {
        valueLabel = value;
        return this;
    }

    /**
     * Sets the "name" part of the entry as optional.
     *
     * @return Field to create
     */
    public NamedEntries nameOptional() {
        this.nameRequired = false;
        return this;
    }

    /**
     * Sets the label for the "add" button
     *
     * @param value Label for the "add" button
     * @return Field to create
     */
    public NamedEntries addText(String value) {
        this.addText = value;
        return this;
    }
}
