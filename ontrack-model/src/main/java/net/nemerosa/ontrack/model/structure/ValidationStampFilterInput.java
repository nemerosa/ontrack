package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import java.util.List;

@Data
public class ValidationStampFilterInput {

    private final String name;
    private final List<String> vsNames;

}
