package net.nemerosa.ontrack.git.model

import org.eclipse.jgit.revwalk.RevCommit

class GitRange(
        val from: RevCommit,
        val to: RevCommit
)
