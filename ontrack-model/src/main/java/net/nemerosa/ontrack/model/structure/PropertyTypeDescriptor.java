package net.nemerosa.ontrack.model.structure;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Serializable variant for a property type.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PropertyTypeDescriptor {

    public static <T> PropertyTypeDescriptor of(PropertyType<T> type) {
        return new PropertyTypeDescriptor(
                type.getClass().getName(),
                type.getName(),
                type.getIconPath(),
                type.getShortTemplatePath(),
                type.getFullTemplatePath()
        );
    }

    private final String typeName;
    private final String name;
    private final String iconPath;
    private final String shortTemplatePath;
    private final String fullTemplatePath;

}
