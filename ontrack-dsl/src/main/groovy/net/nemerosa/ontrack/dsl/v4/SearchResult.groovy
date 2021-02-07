package net.nemerosa.ontrack.dsl.v4

import net.nemerosa.ontrack.dsl.v4.doc.DSL
import net.nemerosa.ontrack.dsl.v4.doc.DSLMethod

@DSL
class SearchResult extends AbstractResource {

    SearchResult(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    @DSLMethod("Gets the display name for the search result.")
    String getTitle() {
        node['title']
    }

    @DSLMethod("Gets a description for the search result.")
    String getDescription() {
        node['description']
    }

    @DSLMethod("Gets the URI to access the search result details (API).")
    String getUri() {
        node['uri']
    }

    @DSLMethod("Gets the URI to display the search result details (Web).")
    String getPage() {
        node['page']
    }

    @DSLMethod("Gets a percentage of accuracy about the result.")
    int getAccuracy() {
        node['accuracy'] as int
    }

}
