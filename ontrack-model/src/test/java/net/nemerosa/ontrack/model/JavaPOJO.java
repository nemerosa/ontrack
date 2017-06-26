package net.nemerosa.ontrack.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class JavaPOJO {

    private final String name;
    private final int value;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public int getDoubleValue() {
        return value * 2;
    }

}
