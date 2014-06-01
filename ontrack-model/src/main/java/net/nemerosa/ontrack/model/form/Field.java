package net.nemerosa.ontrack.model.form;

public interface Field {

    String getType();

    String getName();

    String getLabel();

    String getHelp();

    Field value(Object value);
}
