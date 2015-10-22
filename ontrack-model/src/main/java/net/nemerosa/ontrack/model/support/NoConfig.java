package net.nemerosa.ontrack.model.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class NoConfig {

    public static final NoConfig INSTANCE = new NoConfig();

    @JsonIgnore
    private final String ignored = "";

}
