package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;
import net.nemerosa.ontrack.model.form.Form;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@JsonPropertyOrder({"id", "name", "description", "image"})
public class PredefinedPromotionLevel implements Entity {

    public static PredefinedPromotionLevel of(NameDescription nameDescription) {
        return new PredefinedPromotionLevel(ID.NONE, nameDescription.getName(), nameDescription.getDescription(), false);
    }

    private final ID id;
    private final String name;
    @Wither
    private final String description;
    private final Boolean image;

    public PredefinedPromotionLevel withId(ID id) {
        return new PredefinedPromotionLevel(id, name, description, image);
    }

    public PredefinedPromotionLevel withImage(boolean image) {
        return new PredefinedPromotionLevel(id, name, description, image);
    }

    public static Form form() {
        return Form.nameAndDescription();
    }

    public Form asForm() {
        return form()
                .fill("name", name)
                .fill("description", description);
    }

    public PredefinedPromotionLevel update(NameDescription nameDescription) {
        return new PredefinedPromotionLevel(
                id,
                nameDescription.getName(),
                nameDescription.getDescription(),
                image
        );
    }
}
