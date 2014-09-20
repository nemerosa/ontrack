package net.nemerosa.ontrack.extension.general;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.nemerosa.ontrack.model.support.NameValue;

import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class LinkProperty {

    private final List<NameValue> links;

    public static LinkProperty of(String name, String value) {
        return new LinkProperty(
                Arrays.asList(
                        new NameValue(name, value)
                )
        );
    }
}
