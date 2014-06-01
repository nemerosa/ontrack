package net.nemerosa.ontrack.model.form;

public class YesNo extends AbstractField<YesNo> {

    protected YesNo(String name) {
        super("yesno", name);
    }

    public static YesNo of(String name) {
        return new YesNo(name);
    }


}
