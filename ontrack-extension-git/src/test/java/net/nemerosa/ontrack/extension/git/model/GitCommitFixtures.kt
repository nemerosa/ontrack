package net.nemerosa.ontrack.extension.git.model

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.git.model.GitCommit
import net.nemerosa.ontrack.git.model.GitPerson
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.PromotionRun

object GitCommitFixtures {

    fun testGitUICommit(
        commit: GitCommit = testGitCommit(),
    ) = GitUICommit(
        commit = commit,
        annotatedMessage = commit.fullMessage,
        fullAnnotatedMessage = commit.fullMessage,
        link = "uri:commit/${commit.id}",
    )

    fun testGitCommit() = GitCommit(
        id = "52064610edec86ed5926b813843ed13de8625d4b",
        shortId = "52064610",
        author = GitPerson(
            name = "Damien Coraboeuf",
            email = "damien.coraboeuf@gmail.com"
        ),
        commitTime = Time.now(),
        fullMessage = "This is a commit",
        shortMessage = "This is a commit",
    )

}