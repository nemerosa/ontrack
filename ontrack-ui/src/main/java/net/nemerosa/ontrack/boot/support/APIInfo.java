package net.nemerosa.ontrack.boot.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class APIInfo {

    @JsonIgnore
    private final String type;
    private final String name;
    private final List<APIMethodInfo> methods = new ArrayList<>();

    public APIInfo add(APIMethodInfo methodInfo) {
        methods.add(methodInfo);
        return this;
    }

}
