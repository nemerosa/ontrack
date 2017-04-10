package net.nemerosa.ontrack.model.structure;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;
import net.nemerosa.ontrack.model.form.Form;

/**
 * Validation stamp defined at global level, allowing some projects to create them automatically.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PredefinedValidationStamp implements Entity {

    public static PredefinedValidationStamp of(NameDescription nameDescription) {
        return new PredefinedValidationStamp(ID.NONE, nameDescription.getName(), nameDescription.getDescription(), false);
    }

    private final ID id;
    private final String name;
    @Wither
    private final String description;
    private final Boolean image;

    public PredefinedValidationStamp withId(ID id) {
        return new PredefinedValidationStamp(id, name, description, image);
    }

    public PredefinedValidationStamp withImage(boolean image) {
        return new PredefinedValidationStamp(id, name, description, image);
    }

    public static Form form() {
        return Form.nameAndDescription();
    }

    public Form asForm() {
        return form()
                .fill("name", name)
                .fill("description", description);
    }

    public PredefinedValidationStamp update(NameDescription nameDescription) {
        return new PredefinedValidationStamp(
                id,
                nameDescription.getName(),
                nameDescription.getDescription(),
                image
        );
    }
}
