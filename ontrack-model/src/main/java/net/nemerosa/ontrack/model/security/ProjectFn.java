package net.nemerosa.ontrack.model.security;

import lombok.Data;

@Data
public class ProjectFn {

    private final int id;
    private final Class<? extends ProjectFunction> fn;

}
