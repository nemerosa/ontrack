package net.nemerosa.ontrack.dsl.v4

import net.nemerosa.ontrack.dsl.v4.doc.DSL
import net.nemerosa.ontrack.dsl.v4.doc.DSLMethod

@DSL
class AbstractResource {

    protected final Ontrack ontrack
    protected final Object node

    AbstractResource(Ontrack ontrack, Object node) {
        this.ontrack = ontrack
        this.node = node
    }

    @DSLMethod("Gets the internal JSON representation of this resource.")
    Object getNode() {
        node
    }

    @DSLMethod("Gets a link address.")
    public String link(String name) {
        String link = optionalLink(name)
        if (link) {
            link
        } else {
            throw new ResourceMissingLinkException(name)
        }
    }

    @DSLMethod("Gets the Web page address for this resource.")
    public String getPage() {
        return link('page')
    }

    @DSLMethod("Gets a link address if it exists.")
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
