package net.nemerosa.ontrack.model.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class Password extends AbstractText<Password> {

    protected Password(String name) {
        super("password", name);
    }

    public static Password of(String name) {
        return new Password(name);
    }

}
