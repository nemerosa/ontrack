package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.nemerosa.ontrack.model.support.IDJsonDeserializer;
import net.nemerosa.ontrack.model.support.IDJsonSerializer;
import org.apache.commons.lang3.Validate;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Function;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonSerialize(using = IDJsonSerializer.class)
@JsonDeserialize(using = IDJsonDeserializer.class)
public final class ID implements Serializable {

    /**
     * Undefined ID.
     * <p>
     * Its integer value is <code>0</code> and a call to {@link #isSet()} returns <code>false</code>.
     */
    public static ID NONE = new ID(0);

    /**
     * Builds a <i>defined</i> ID. The given <code>value</code> must be an integer greater than 0.
     *
     * @param value The concrete ID value
     * @return A defined ID.
     * @throws java.lang.IllegalArgumentException If <code>value</code> is less or equal than 0.
     */
    public static ID of(int value) {
        Validate.isTrue(value > 0, "ID value must be greater than zero.");
        return new ID(value);
    }

    private final int value;

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public int get() {
        return value;
    }

    public boolean isSet() {
        return value > 0;
    }

    public static boolean isDefined(ID id) {
        return id != null && id.isSet();
    }

    public <T> Optional<T> ifSet(Function<Integer, T> fn) {
        if (isSet()) {
            return Optional.ofNullable(fn.apply(value));
        } else {
            return Optional.empty();
        }
    }
}
