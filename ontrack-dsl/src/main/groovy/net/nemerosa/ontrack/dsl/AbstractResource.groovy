package net.nemerosa.ontrack.dsl

class AbstractResource {

    protected final Ontrack ontrack
    protected final Object node

    AbstractResource(Ontrack ontrack, Object node) {
        this.ontrack = ontrack
        this.node = node
    }

    protected String link(String name) {
        String linkName = name.startsWith('_') ? name : '_' + name
        if (node[linkName]) {
            node[linkName]
        } else {
            throw new ResourceMissingLinkException(name);
        }
    }

    protected static String query(String url, Map<String, ?> parameters) {
        if (parameters.empty) {
            url
        } else {
            "${url}?${parameters.collect { k, v -> "$k=${URLEncoder.encode(v as String, 'UTF-8')}" }.join('&')}"
        }
    }

    protected List<Object> list(String url) {
        ontrack.get(url).resources as List
    }

    protected Object get(String url) {
        ontrack.get(url)
    }

    protected Object post(String url, data) {
        ontrack.post(url, data)
    }

    protected Object put(String url, data) {
        ontrack.put(url, data)
    }

    @Override
    String toString() {
        node as String
    }
}
