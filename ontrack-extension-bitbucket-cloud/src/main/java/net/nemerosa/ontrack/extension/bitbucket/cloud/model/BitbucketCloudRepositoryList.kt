package net.nemerosa.ontrack.extension.bitbucket.cloud.model

/**
 * List of repositories.
 *
 * @property values List of repositories in the page
 * @property next Link to the next page if any
 * @property page Current page index
 */
class BitbucketCloudRepositoryList(
    override val values: List<BitbucketCloudRepository>,
    override val next: String?,
    override val page: Int,
) : BitbucketCloudPaginatedList<BitbucketCloudRepository>
