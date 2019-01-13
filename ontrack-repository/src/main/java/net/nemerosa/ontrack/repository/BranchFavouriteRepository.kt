package net.nemerosa.ontrack.repository

/**
 * Management of favourite branches
 */
interface BranchFavouriteRepository {

    /**
     * Is this branch a favourite?
     */
    fun isBranchFavourite(accountId: Int, branchId: Int): Boolean

    /**
     * Sets a branch as favourite (or not)
     */
    fun setBranchFavourite(accountId: Int, branchId: Int, favourite: Boolean)

}