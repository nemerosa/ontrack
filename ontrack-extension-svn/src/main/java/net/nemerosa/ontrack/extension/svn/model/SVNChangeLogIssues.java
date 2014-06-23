package net.nemerosa.ontrack.extension.svn.model;

import lombok.Data;

import java.util.List;

@Data
public class SVNChangeLogIssues {

    private final String allIssuesLink;
    private final List<SVNChangeLogIssue> list;

}
