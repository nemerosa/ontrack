package net.nemerosa.ontrack.boot.resource;

import lombok.Data;
import org.springframework.web.util.UriComponentsBuilder;

@Data
public class Link {

    private final String uri;

    public static String link(UriComponentsBuilder uriComponentsBuilder, Object... uriVariables) {
        return uriComponentsBuilder.buildAndExpand(uriVariables).encode().toUriString();
    }
}
