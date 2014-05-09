package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.nemerosa.ontrack.model.support.IDJsonSerializer;
import org.apache.commons.lang3.Validate;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonSerialize(using = IDJsonSerializer.class)
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
