package net.nemerosa.ontrack.model.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class Text extends AbstractText<Text> {

    private String regex = ".*";

    protected Text(String name) {
        super("text", name);
    }

    public static Text of(String name) {
        return new Text(name);
    }

    public Text regex(String value) {
        this.regex = value;
        return this;
    }

}
