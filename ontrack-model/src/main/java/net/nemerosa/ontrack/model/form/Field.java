package net.nemerosa.ontrack.model.form;

public interface Field {

    String getType();

    String getName();

    String getLabel();

    Field value(Object value);
}
