package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.structure.ID;

import java.util.Collection;

public interface BuildFilterRepository {

    Collection<TBuildFilter> findForBranch(int accountId, int branchId);

}
