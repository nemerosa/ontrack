package net.nemerosa.ontrack.boot.resource;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

@EqualsAndHashCode
@ToString
@JsonSerialize(using = ResourceJsonSerializer.class)
public class Resource<T> {

    public static final String SELF = "self";
    private final T data;
    private final Map<String, Link<?>> links = new LinkedHashMap<>();

    protected Resource(T data) {
        this.data = data;
    }

    public static <T> Resource<T> of(T data) {
        return new Resource<>(data);
    }

    public static Resource<Object> empty() {
        return new Resource<>(null);
    }

    public static String link(UriComponentsBuilder uriComponentsBuilder, Object... uriVariables) {
        return uriComponentsBuilder.buildAndExpand(uriVariables).encode().toUriString();
    }

    public Resource<T> self(String uri) {
        return link(SELF, uri);
    }

    public Resource<T> link(String rel, String uri) {
        links.put(rel, new Link<>(uri));
        return this;
    }

    public <L> Resource<T> link(String rel, String uri, Supplier<Resource<L>> supplier) {
        links.put(rel, new Link<>(uri, supplier));
        return this;
    }

    public <L> Resource<T> link(String rel, String uri, Resource<L> resource) {
        links.put(rel, new Link<>(uri, resource));
        return this;
    }

    public T getData() {
        return data;
    }

    public Map<String, Link<?>> getLinks() {
        return links;
    }

    public Resource<T> follow(Set<String> follow) {
        Resource<T> resource = this;
        if (follow != null) {
            for (String link : follow) {
                resource = resource.follow(link);
            }
        }
        return resource;
    }

    public Resource<T> follow(String rel) {
        String node = StringUtils.substringBefore(rel, ".");
        String rest = StringUtils.substringAfterLast(rel, ".");
        Link<?> ln = links.get(node);
        if (ln != null) {
            return follow(rel, ln, rest);
        } else {
            throw new LinkNotFoundException(node);
        }
    }

    private <L> Resource<T> follow(String rel, Link<L> link, String rest) {
        Supplier<Resource<L>> supplier = link.getSupplier();
        if (supplier == null) throw new LinkNoSupplierException(link.getUri());
        Resource<L> linkedResource = supplier.get();
        // Going on with the rest of the links to follow
        if (StringUtils.isNotBlank(rest)) {
            linkedResource = linkedResource.follow(rest);
        }
        // Changes the link
        links.put(rel, link.with(linkedResource));
        // OK
        return this;
    }
}
