package net.nemerosa.ontrack.model.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class Date extends AbstractField<Date> {

    public static Date of(String name) {
        return new Date(name);
    }

    protected Date(String name) {
        super("date", name);
    }

}