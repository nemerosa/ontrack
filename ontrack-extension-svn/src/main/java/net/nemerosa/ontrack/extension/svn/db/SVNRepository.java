package net.nemerosa.ontrack.extension.svn.db;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.nemerosa.ontrack.extension.svn.SVNConfiguration;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SVNRepository {

    private final int id;
    private final SVNConfiguration configuration;

    public static SVNRepository of(int id, SVNConfiguration configuration) {
        return new SVNRepository(id, configuration);
    }
}
