package net.nemerosa.ontrack.model.form;

import org.apache.commons.lang3.Validate;

public class DateTime extends AbstractField<DateTime> {

    public static DateTime of(String name) {
        return new DateTime(name);
    }

    private int minuteStep = 1;

    protected DateTime(String name) {
        super("dateTime", name);
    }

    public DateTime minuteStep(int value) {
        Validate.inclusiveBetween(0, 60, value);
        this.minuteStep = value;
        return this;
    }
}
