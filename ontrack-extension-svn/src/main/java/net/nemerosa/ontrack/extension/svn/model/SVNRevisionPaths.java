package net.nemerosa.ontrack.extension.svn.model;

import lombok.Data;

import java.util.List;

@Data
public class SVNRevisionPaths {

    private final SVNRevisionInfo info;
    private final List<SVNRevisionPath> paths;

}
