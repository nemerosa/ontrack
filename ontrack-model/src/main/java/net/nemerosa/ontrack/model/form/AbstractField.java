package net.nemerosa.ontrack.model.form;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public abstract class AbstractField<F extends AbstractField<F>> implements Field {

    private final String type;
    private final String name;
    private String label;
    private boolean required = true;
    private boolean readOnly = false;
    private String validation;
    private String help = "";
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String visibleIf;
    private Object value;

    protected AbstractField(String type, String name) {
        this.type = type;
        this.name = name;
        this.label = name;
    }

    public F optional() {
        return optional(true);
    }

    public F optional(boolean optional) {
        this.required = !optional;
        //noinspection unchecked
        return (F) this;
    }

    public F readOnly() {
        return readOnly(true);
    }

    public F readOnly(boolean readOnly) {
        this.readOnly = readOnly;
        //noinspection unchecked
        return (F) this;
    }

    public F visibleIf(String value) {
        this.visibleIf = value;
        //noinspection unchecked
        return (F) this;
    }

    public F label(String label) {
        this.label = label;
        //noinspection unchecked
        return (F) this;
    }

    public F validation(String value) {
        this.validation = value;
        //noinspection unchecked
        return (F) this;
    }

    /**
     * Adds some help text to the field. If the help text is long or needs some HTML formatting,
     * use the <code>@file:</code> prefix and append the path to an HTML fragment file. The path
     * to this file is relative to the <i>src/app</i> of the static Web application.
     * <p>
     * For an extension, prefix the path with the extension path, for example <i>extension/git/</i>.
     * <p>
     * For the actual HTML file, use the <i>html.$class.$field.tpl.html</i> format, for example:
     * <i>help.net.nemerosa.ontrack.extension.git.model.GitConfiguration.indexationInterval.tpl.html</i>.
     */
    public F help(String value) {
        this.help = value;
        //noinspection unchecked
        return (F) this;
    }

    @Override
    public F value(Object value) {
        this.value = value;
        //noinspection unchecked
        return (F) this;
    }
}
