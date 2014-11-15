package net.nemerosa.ontrack.extension.git.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class NoConfig {

    @JsonIgnore
    private final String ignored = "";

}
