package net.nemerosa.ontrack.model.form;

import lombok.Data;

@Data
public abstract class AbstractField<F extends AbstractField<F>> implements Field {

    private final String type;
    private final String name;
    private String label;
    private boolean required = true;
    private boolean readOnly = false;
    private String validation;
    private String help = "";
    private Object value;

    protected AbstractField(String type, String name) {
        this.type = type;
        this.name = name;
        this.label = name;
    }

    public F optional() {
        this.required = false;
        //noinspection unchecked
        return (F) this;
    }

    public F readOnly() {
        this.readOnly = true;
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

    public F help(String value) {
        this.help = value;
        //noinspection unchecked
        return (F) this;
    }

    @Override
    public F value(Object value) {
        this.value = value;
        //noinspection unchecked
        return (F) this;
    }
}
