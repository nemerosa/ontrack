package net.nemerosa.ontrack.boot.support;

import lombok.Data;

import java.util.List;

@Data
public class APIMethodInfo {

    private final String name;
    private final String path;
    private final List<String> methods;

    public APIMethodInfo(String name, String path, List<String> methods) {
        this.name = name;
        this.path = path;
        this.methods = methods;
    }

    public static APIMethodInfo of(String name, String path, List<String> methods) {
        return new APIMethodInfo(name, path, methods);
    }
}
