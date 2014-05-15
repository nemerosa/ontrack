package net.nemerosa.ontrack.ui.resource;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.Data;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public abstract class LinkContainer<L extends LinkContainer<L>> {

    private final URI href;
    private final Map<String, Link> links = new LinkedHashMap<>();

    public L with(String name, URI uri) {
        return with(Link.of(name, uri));
    }

    private L with(Link link) {
        links.put(link.getName(), link);
        //noinspection unchecked
        return (L) this;
    }

    @JsonAnyGetter
    public Map<String, Link> getLinks() {
        return links;
    }

}
