package net.nemerosa.ontrack.model.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class Int extends AbstractField<Int> {

    private int min = 0;
    private int max = Integer.MAX_VALUE;
    private int step = 1;

    protected Int(String name) {
        super("int", name);
    }

    public static Int of(String name) {
        return new Int(name);
    }

    public Int min(int value) {
        this.min = value;
        return this;
    }

    public Int max(int value) {
        this.max = value;
        return this;
    }

    public Int step(int value) {
        this.step = value;
        return this;
    }

}
