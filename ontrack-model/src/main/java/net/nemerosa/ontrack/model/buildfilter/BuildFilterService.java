package net.nemerosa.ontrack.model.buildfilter;

import net.nemerosa.ontrack.model.structure.ID;

import java.util.Map;

public interface BuildFilterService {

    BuildFilter defaultFilter();

    BuildFilters getBuildFilters(ID branchId);

    BuildFilter computeFilter(ID branchId, Map<String, String> parameters);
}
