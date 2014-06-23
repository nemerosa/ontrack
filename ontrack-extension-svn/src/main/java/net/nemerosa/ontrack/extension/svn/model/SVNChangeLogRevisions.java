package net.nemerosa.ontrack.extension.svn.model;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class SVNChangeLogRevisions {

    private final List<SVNChangeLogRevision> list;

    public static SVNChangeLogRevisions none() {
        return new SVNChangeLogRevisions(Collections.<SVNChangeLogRevision>emptyList());
    }

}
