package net.nemerosa.ontrack.service.support.property;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class TestProperty {

    private final String value;

    public static TestProperty of(String value) {
        return new TestProperty(value);
    }
}
