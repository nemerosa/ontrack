package net.nemerosa.ontrack.model.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class Email extends AbstractText<Email> {

    protected Email(String name) {
        super("email", name);
    }

    public static Email of(String name) {
        return new Email(name);
    }

}
