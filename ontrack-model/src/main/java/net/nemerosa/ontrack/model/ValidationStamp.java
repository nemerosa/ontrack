package net.nemerosa.ontrack.model;

import lombok.Data;

import java.util.Optional;

@Data
public class ValidationStamp {

    private final String id;
    private final String name;
    private final String description;
    private final Branch branch;
    private final Optional<User> owner;

}
