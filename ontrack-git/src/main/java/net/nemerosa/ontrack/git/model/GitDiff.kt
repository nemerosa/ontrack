package net.nemerosa.ontrack.git.model

import org.eclipse.jgit.revwalk.RevCommit

class GitDiff(
        val from: RevCommit,
        val to: RevCommit,
        val entries: List<GitDiffEntry>
)
