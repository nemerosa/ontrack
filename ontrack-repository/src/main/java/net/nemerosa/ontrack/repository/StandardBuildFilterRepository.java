package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.StandardBuildFilterData;

import java.util.List;

/**
 * Standard filter query.
 */
public interface StandardBuildFilterRepository {

    List<Build> getBuilds(ID branchId, StandardBuildFilterData data);

}
