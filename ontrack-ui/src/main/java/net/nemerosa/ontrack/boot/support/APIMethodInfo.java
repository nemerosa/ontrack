package net.nemerosa.ontrack.boot.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class APIMethodInfo {

    @JsonIgnore
    private final APIInfo apiInfo;
    @JsonIgnore
    private final String method;
    private final String name;
    private final String path;
    private final List<String> methods;

    public APIMethodInfo(APIInfo apiInfo, String method, String name, String path, List<String> methods) {
        this.apiInfo = apiInfo;
        this.method = method;
        this.name = name;
        this.path = path;
        this.methods = methods;
    }

    public static APIMethodInfo of(APIInfo apiInfo, String method, String name, String path, List<String> methods) {
        return new APIMethodInfo(apiInfo, method, name, path, methods);
    }
}
