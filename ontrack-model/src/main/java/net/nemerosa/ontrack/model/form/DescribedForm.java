package net.nemerosa.ontrack.model.form;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.URI;

/**
 * {@link Form} associated with title, description and URI.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class DescribedForm {

    private final String id;
    private final String title;
    private final String description;
    private final URI uri;
    private final Form form;

    public static DescribedForm create(String id, Form form) {
        return new DescribedForm(id, "", "", null, form);
    }

    public DescribedForm title(String value) {
        return new DescribedForm(
                id,
                value,
                description,
                uri,
                form
        );
    }

    public DescribedForm description(String value) {
        return new DescribedForm(
                id,
                title,
                value,
                uri,
                form
        );
    }

    public DescribedForm uri(URI value) {
        return new DescribedForm(
                id,
                title,
                description,
                value,
                form
        );
    }
}
