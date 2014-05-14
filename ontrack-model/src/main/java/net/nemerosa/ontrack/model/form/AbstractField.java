package net.nemerosa.ontrack.model.form;

import lombok.Data;

@Data
public abstract class AbstractField<F extends AbstractField<F>> implements Field {

    private final String name;
    private String label;
    private boolean required = true;
    private String validation;
    private Object value;

    protected AbstractField(String name) {
        this.name = name;
        this.label = name;
    }

    public F optional() {
        this.required = false;
        //noinspection unchecked
        return (F) this;
    }

    public F label(String label) {
        this.label = label;
        //noinspection unchecked
        return (F) this;
    }

    public F validation(String value) {
        this.validation = value;
        //noinspection unchecked
        return (F) this;
    }

    @Override
    public Field value(Object value) {
        this.value = value;
        return this;
    }
}
