package net.nemerosa.ontrack.model.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This field allows a user to select and create a list of strings.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class MultiStrings extends AbstractField<MultiStrings> {

    public static MultiStrings of(String name) {
        return new MultiStrings(name);
    }

    protected MultiStrings(String name) {
        super("multi-strings", name);
    }

}
