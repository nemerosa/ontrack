package net.nemerosa.ontrack.model.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class Url extends AbstractText<Url> {

    protected Url(String name) {
        super("url", name);
    }

    public static Url of() {
        return of("url").label("URL");
    }

    public static Url of(String name) {
        return new Url(name);
    }

}
