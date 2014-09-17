package net.nemerosa.ontrack.model.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Field used to define a list of {@link net.nemerosa.ontrack.model.form.Replacement}s.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class Replacements extends AbstractField<Replacements> {

    protected Replacements(String name) {
        super("replacements", name);
    }

    public static Replacements of(String name) {
        return new Replacements(name);
    }

}
