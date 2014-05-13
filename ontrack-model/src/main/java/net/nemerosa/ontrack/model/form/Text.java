package net.nemerosa.ontrack.model.form;

import lombok.Data;

@Data
public class Text extends AbstractText<Text> {

    private String regex;

    protected Text(String name) {
        super(name);
    }

    public static Text of(String name) {
        return new Text(name);
    }

    public Text regex(String value) {
        this.regex = value;
        return this;
    }

}
