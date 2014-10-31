package net.nemerosa.ontrack.boot.support;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class APIInfo {

    private final String name;
    private final List<APIMethodInfo> methods = new ArrayList<>();

    public APIInfo add(APIMethodInfo methodInfo) {
        methods.add(methodInfo);
        return this;
    }

}
