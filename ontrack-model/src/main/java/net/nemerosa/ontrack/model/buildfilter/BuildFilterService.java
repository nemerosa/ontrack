package net.nemerosa.ontrack.model.buildfilter;

import net.nemerosa.ontrack.model.structure.ID;

public interface BuildFilterService {

    BuildFilter defaultFilter();

    BuildFilters getBuildFilters(ID branchId);
}
