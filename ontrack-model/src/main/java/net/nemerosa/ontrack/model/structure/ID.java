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

    public static ID NONE = new ID(0);

    public static ID of(int value) {
        Validate.isTrue(value > 0, "ID value must be greater than zero.");
        return new ID(value);
    }

    private final int value;

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public boolean isSet() {
        return value > 0;
    }

    public static boolean isDefined(ID id) {
        return id != null && id.isSet();
    }
}
