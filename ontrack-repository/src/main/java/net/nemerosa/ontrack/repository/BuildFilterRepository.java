package net.nemerosa.ontrack.repository;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.model.Ack;

import java.util.Collection;
import java.util.Optional;
import java.util.OptionalInt;

public interface BuildFilterRepository {

    Collection<TBuildFilter> findForBranch(int branchId);

    Collection<TBuildFilter> findForBranch(OptionalInt accountId, int branchId);

    Optional<TBuildFilter> findByBranchAndName(int accountId, int branchId, String name);

    Ack save(OptionalInt accountId, int branchId, String name, String type, JsonNode data);

    /**
     * Deletes a filter from a branch and for an account. If <code>shared</code> is <code>true</code>,
     * we have also to delete it from the shared filters.
     *
     * @param accountId Account to delete the filter from
     * @param branchId  Branch to delete the filter from
     * @param name      Name of the filter to delete
     * @param shared    If set, deletes the filter from the shared ones as well
     * @return If a filter was deleted
     */
    Ack delete(int accountId, int branchId, String name, boolean shared);
}
