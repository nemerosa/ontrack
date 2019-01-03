package net.nemerosa.ontrack.model.form;

public class Color extends AbstractField<Color> {

    protected Color(String name) {
        super("color", name);
    }

    public static Color of(String name) {
        return new Color(name);
    }


}
