package net.nemerosa.ontrack.dsl

class SearchResult extends AbstractResource {

    SearchResult(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    String getTitle() {
        node['title']
    }

    String getDescription() {
        node['description']
    }

    String getUri() {
        node['uri']
    }

    String getPage() {
        node['page']
    }

    int getAccuracy() {
        node['accuracy'] as int
    }

}
