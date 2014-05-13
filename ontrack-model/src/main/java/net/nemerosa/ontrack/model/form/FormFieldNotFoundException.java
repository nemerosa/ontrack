package net.nemerosa.ontrack.model.form;

import net.nemerosa.ontrack.model.exceptions.BaseException;

public class FormFieldNotFoundException extends BaseException {
    public FormFieldNotFoundException(String name) {
        super("Field %s not found in form.", name);
    }
}
