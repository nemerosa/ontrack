package net.nemerosa.ontrack.model.buildfilter;

import net.nemerosa.ontrack.model.structure.ID;

public interface BuildFilterProvider {

    /**
     * Gets the form for a new filter on the given branch
     */
    BuildFilterForm newFilterForm(ID branchId);

}
