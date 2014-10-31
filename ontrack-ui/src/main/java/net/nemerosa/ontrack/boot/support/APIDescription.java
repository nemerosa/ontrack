package net.nemerosa.ontrack.boot.support;

import lombok.Data;

import java.util.List;

@Data
public class APIDescription {

    private final String path;
    private final List<APIMethodInfo> methods;

}
