package net.nemerosa.ontrack.model.structure;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.Validate;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ID {

    public static ID NONE = new ID(null);

    public static ID of(String value) {
        Validate.notBlank(value, "ID value must not be blank");
        return new ID(value);
    }

    private final String value;

    @Override
    public String toString() {
        return value;
    }

    public boolean isSet() {
        return value != null && value.length() > 0;
    }

}
