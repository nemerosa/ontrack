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

    public String getPage() {
        return link('page')
    }

    public String optionalLink(String name) {
        String linkName = name.startsWith('_') ? name : '_' + name
        if (node[linkName]) {
            node[linkName]
        } else {
            null
        }
    }

    public static String query(String url, Map<String, ?> parameters) {
        // Cleanup
        String base
        int pos = url.indexOf('?')
        if (pos < 0) {
            base = url
        } else {
            base = url.substring(0, pos)
        }
        // Parameters
        if (parameters == null || parameters.empty) {
            return base
        } else {
            return "${base}?${parameters.collect { k, v -> "$k=${URLEncoder.encode(v as String, 'UTF-8')}" }.join('&')}"
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
