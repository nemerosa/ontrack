package net.nemerosa.ontrack.ui.resource;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.Data;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public abstract class LinkContainer<L extends LinkContainer<L>> {

    private final URI _self;
    private final Map<String, URI> links;

    protected LinkContainer(URI uri) {
        this(uri, new LinkedHashMap<>());
    }

    protected LinkContainer(URI uri, Map<String, URI> links) {
        this._self = uri;
        this.links = links;
    }

    public L with(String name, URI uri, boolean authorized) {
        if (authorized) {
            return with(name, uri);
        } else {
            //noinspection unchecked
            return (L) this;
        }
    }

    public L with(String name, URI uri) {
        links.put(name, uri);
        //noinspection unchecked
        return (L) this;
    }

    @JsonAnyGetter
    public Map<String, URI> getLinks() {
        return links;
    }

    public L withLinks(Map<String, URI> links) {
        this.links.putAll(links);
        //noinspection unchecked
        return (L) this;
    }
}
