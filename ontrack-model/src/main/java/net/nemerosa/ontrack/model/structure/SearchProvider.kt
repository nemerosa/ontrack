package net.nemerosa.ontrack.model.structure

@Deprecated(
        message = "Must be replaced by `SearchIndexer`. The `SearchProvider` interface will be removed in version 4.0.",
        replaceWith = ReplaceWith("SearchIndexer")
)
interface SearchProvider {

    fun isTokenSearchable(token: String): Boolean
    fun search(token: String): Collection<SearchResult>
}