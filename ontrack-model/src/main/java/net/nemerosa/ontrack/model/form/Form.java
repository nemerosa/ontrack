package net.nemerosa.ontrack.model.form;

import lombok.Data;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class Form {

    public static Form nameAndDescription() {
        return Form.create().name().description();
    }

    public static Form create() {
        return new Form();
    }

    private final Map<String, Field> fields = new LinkedHashMap<>();

    public Form name() {
        return with(
                defaultNameField()
        );
    }

    public Form password() {
        return with(
                Password.of("password")
                        .label("Password")
                        .length(40)
                        .validation("Password is required.")
        );
    }

    public Form description() {
        return with(
                Memo.of("description")
                        .label("Description")
                        .optional()
                        .length(500)
                        .rows(3)
        );
    }

    public Form dateTime() {
        return with(
                DateTime.of("dateTime")
                        .label("Date/time")
        );
    }

    public Form url() {
        return with(Url.of());
    }

    public Form with(Field field) {
        fields.put(field.getName(), field);
        return this;
    }

    public Form with(Iterable<Field> fields) {
        fields.forEach(field ->
                this.fields.put(field.getName(), field)
        );
        return this;
    }

    public Field getField(String name) {
        return fields.get(name);
    }

    public Collection<? extends Field> getFields() {
        return fields.values();
    }

    public Form name(String value) {
        return fill("name", value);
    }

    public Form description(String value) {
        return fill("description", value);
    }

    public Form fill(String name, Object value) {
        Field field = fields.get(name);
        if (field != null) {
            field = field.value(value);
            fields.put(name, field);
        } else {
            throw new FormFieldNotFoundException(name);
        }
        return this;
    }

    public Form fill(Map<String, ?> data) {
        data.forEach((name, value) -> {
            if (fields.containsKey(name)) {
                fill(name, value);
            }
        });
        return this;
    }

    public static Text defaultNameField() {
        return Text.of("name")
                .label("Name")
                .length(40)
                .regex("[A-Za-z0-9_\\.\\-]+")
                .validation("Name is required and must contain only alpha-numeric characters, underscores, points or dashes.");
    }

    public Form append(Form form) {
        this.fields.putAll(form.fields);
        return this;
    }
}
