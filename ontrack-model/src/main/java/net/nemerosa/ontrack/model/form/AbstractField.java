package net.nemerosa.ontrack.model.form;

import lombok.Data;

@Data
public abstract class AbstractField<F extends AbstractField<F>> implements Field {

    private final String name;
    private String label;
    private Object value;

    protected AbstractField(String name) {
        this.name = name;
        this.label = name;
    }

    F label(String label) {
        this.label = label;
        return (F) this;
    }

    @Override
    public Field value(Object value) {
        this.value = value;
        return this;
    }
}
