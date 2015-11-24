package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.nemerosa.ontrack.model.extension.ExtensionFeatureDescription;
import org.apache.commons.lang3.Validate;

/**
 * Decoration for an entity.
 *
 * @param <T> Type of data contained by the entity
 */
@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Decoration<T> {

    /**
     * Which {@link Decorator} has created this decoration
     */
    @JsonIgnore
    private final Decorator<T> decorator;

    /**
     * Data associated with the decoration
     */
    private final T data;

    /**
     * Basic construction. Only the data is required
     */
    public static <T> Decoration<T> of(Decorator<T> decorator, T data) {
        Validate.notNull(decorator, "The decorator is required");
        Validate.notNull(data, "The decoration data is required");
        return new Decoration<>(decorator, data);
    }

    /**
     * Gets the decoration type for the decorator name.
     */
    public String getDecorationType() {
        return decorator.getClass().getName();
    }

    /**
     * Extension feature description
     */
    public ExtensionFeatureDescription getFeature() {
        return decorator.getFeature().getFeatureDescription();
    }

}
