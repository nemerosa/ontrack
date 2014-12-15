package net.nemerosa.ontrack.git.model;

import lombok.Data;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.List;

@Data
public class GitDiff {

    private final RevCommit from;
    private final RevCommit to;
    private final List<GitDiffEntry> entries;

}
