package net.nemerosa.ontrack.extension.bitbucket.cloud.model

/**
 * Paginated list.
 *
 * @param <T> Type of item in this page
 */
interface BitbucketCloudPaginatedList<T> {
    /**
     * List of values in this page
     */
    val values: List<T>

    /**
     *Link to the next page if any
     */
    val next: String?

    /**
     * Current page index
     */
    val page: Int
}
