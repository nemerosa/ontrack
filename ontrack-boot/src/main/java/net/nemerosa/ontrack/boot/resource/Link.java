package net.nemerosa.ontrack.boot.resource;

import lombok.Data;

import java.util.function.Supplier;

@Data
public class Link<T> {

    private final String uri;
    private final Supplier<T> supplier;

}
