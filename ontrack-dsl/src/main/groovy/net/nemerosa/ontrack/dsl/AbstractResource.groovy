package net.nemerosa.ontrack.dsl

class AbstractResource {

    protected final Ontrack ontrack
    protected final Object node

    AbstractResource(Ontrack ontrack, Object node) {
        this.ontrack = ontrack
        this.node = node
    }

    Object getNode() {
        node
    }

    public String link(String name) {
        String link = optionalLink(name)
        if (link) {
            link
        } else {
            throw new ResourceMissingLinkException(name)
        }
    }

    public String optionalLink(String name) {
        String linkName = name.startsWith('_') ? name : '_' + name
        if (node[linkName]) {
            node[linkName]
        } else {
            null
        }
    }

    protected static String query(String url, Map<String, ?> parameters) {
        if (parameters == null || parameters.empty) {
            url
        } else {
            "${url}?${parameters.collect { k, v -> "$k=${URLEncoder.encode(v as String, 'UTF-8')}" }.join('&')}"
        }
    }

    protected List<Object> list(String url) {
        ontrack.get(url).resources as List
    }

    @Override
    String toString() {
        node as String
    }
}
