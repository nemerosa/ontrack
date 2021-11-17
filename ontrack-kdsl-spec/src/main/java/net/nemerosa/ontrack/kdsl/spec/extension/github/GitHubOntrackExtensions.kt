package net.nemerosa.ontrack.kdsl.spec.extension.github

import net.nemerosa.ontrack.kdsl.spec.Ontrack

/**
 * Management of GitHub in Ontrack.
 */
val Ontrack.gitHub: GitHubMgt by lazy {
    GitHubMgt()
}
