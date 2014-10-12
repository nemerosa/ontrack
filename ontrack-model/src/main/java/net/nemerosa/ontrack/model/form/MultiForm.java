package net.nemerosa.ontrack.model.form;

public class MultiForm extends AbstractField<MultiForm> {

    private final Form form;

    protected MultiForm(String name, Form form) {
        super("multi-form", name);
        this.form = form;
    }

    public static MultiForm of(String name, Form form) {
        return new MultiForm(name, form);
    }
}
