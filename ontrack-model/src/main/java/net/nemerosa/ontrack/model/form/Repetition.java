package net.nemerosa.ontrack.model.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class Repetition<F extends Field> extends AbstractField<Repetition<F>> {

    private final F field;
    private String fieldAddText = "Add an item";

    public Repetition(String name, F field) {
        super("repetition", name);
        this.field = field;
    }

    public static <F extends Field> Repetition<F> of(String name, F field) {
        return new Repetition<>(name, field);
    }

    public Repetition<F> fieldAddText(String value) {
        this.fieldAddText = value;
        return this;
    }
}
