package net.nemerosa.ontrack.repository

/**
 * Management of favourite branches
 */
interface BranchFavouriteRepository {

    /**
     * Gets the list of favourites branches for a user
     *
     * @param accountId ID of the user
     * @return List of branch IDs
     */
    fun getFavouriteBranches(accountId: Int): List<Int>

    /**
     * Is this branch a favourite?
     */
    fun isBranchFavourite(accountId: Int, branchId: Int): Boolean

    /**
     * Sets a branch as favourite (or not)
     */
    fun setBranchFavourite(accountId: Int, branchId: Int, favourite: Boolean)

}