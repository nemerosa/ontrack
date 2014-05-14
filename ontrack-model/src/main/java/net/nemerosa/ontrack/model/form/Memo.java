package net.nemerosa.ontrack.model.form;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.Validate;

@EqualsAndHashCode(callSuper = false)
@Data
public class Memo extends AbstractText<Memo> {

    private int rows = 3;

    protected Memo(String name) {
        super("memo", name);
    }

    public static Memo of(String name) {
        return new Memo(name);
    }

    public Memo rows(int value) {
        Validate.isTrue(value > 0);
        this.rows = value;
        return this;
    }

}
