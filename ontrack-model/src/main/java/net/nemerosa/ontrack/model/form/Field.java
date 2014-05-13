package net.nemerosa.ontrack.model.form;

public interface Field {

    String getName();

    String getLabel();

    Field value(Object value);
}
