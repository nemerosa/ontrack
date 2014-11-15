package net.nemerosa.ontrack.extension.git.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class NoConfig {

    public static final NoConfig INSTANCE = new NoConfig();

    @JsonIgnore
    private final String ignored = "";

}
