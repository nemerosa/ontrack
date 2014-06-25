package net.nemerosa.ontrack.extension.svn.model;

import lombok.Data;

import java.util.List;

@Data
public class SVNChangeLogFiles {

    private final List<SVNChangeLogFile> list;

}
