package net.nemerosa.ontrack.boot.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import net.nemerosa.ontrack.model.structure.NameDescription;

import java.util.ArrayList;
import java.util.List;

@Data
public class APIInfo {

    @JsonIgnore
    private final String type;
    private final String name;
    private final String description;
    private final List<APIMethodInfo> methods = new ArrayList<>();

    public APIInfo(String type, NameDescription nd) {
        this.type = type;
        this.name = nd.getName();
        this.description = nd.getDescription();
    }

    public APIInfo add(APIMethodInfo methodInfo) {
        methods.add(methodInfo);
        return this;
    }

}
