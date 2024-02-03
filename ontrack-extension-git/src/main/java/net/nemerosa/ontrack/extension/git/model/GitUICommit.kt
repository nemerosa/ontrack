package net.nemerosa.ontrack.extension.git.model

import net.nemerosa.ontrack.extension.scm.changelog.SCMChangeLogCommit
import net.nemerosa.ontrack.git.model.GitCommit

data class GitUICommit(
    val commit: GitCommit,
    val annotatedMessage: String,
    val fullAnnotatedMessage: String,
    override val link: String,
) : SCMChangeLogCommit {

    override val message = commit.fullMessage

    override val id: String = commit.id

    override val author = commit.author.name
    override val authorEmail = commit.author.email

    override val timestamp = commit.commitTime

    override val formattedMessage = fullAnnotatedMessage

    override val shortId = commit.shortId
}
