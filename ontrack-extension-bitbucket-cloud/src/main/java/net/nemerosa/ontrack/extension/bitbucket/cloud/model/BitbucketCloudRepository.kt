package net.nemerosa.ontrack.extension.bitbucket.cloud.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Representation of a "repository" in Bitbucket Cloud
 *
 * @property uuid Technical ID for the repository
 * @property slug Repository slug
 * @property name Display name for the repository
 * @property updated_on Last updated date
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class BitbucketCloudRepository(
    val uuid: String,
    val slug: String,
    val name: String,
    val updated_on: String,
)
