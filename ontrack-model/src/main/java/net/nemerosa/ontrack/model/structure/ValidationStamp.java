package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import java.util.Optional;

@Data
public class ValidationStamp {

    private final ID id;
    private final String name;
    private final String description;
    @JsonView({})
    private final Branch branch;
    private final Optional<User> owner;

}
