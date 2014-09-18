package net.nemerosa.ontrack.model.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class Repetition<F extends Field> extends AbstractField<Repetition<F>> {

    private final F field;

    public Repetition(String name, F field) {
        super("repetition", name);
        this.field = field;
    }

    public static <F extends Field> Repetition<F> of(String name, F field) {
        return new Repetition<>(name, field);
    }
}
