package net.nemerosa.ontrack.model.structure

interface BranchFavouriteService {

    /**
     * Gets the list of favourites branches for the current user
     *
     * @return List of branches
     */
    fun getFavouriteBranches(): List<Branch>

    /**
     * Is this branch a favourite?
     */
    fun isBranchFavourite(branch: Branch): Boolean

    /**
     * Sets a branch as favourite (or not)
     */
    fun setBranchFavourite(branch: Branch, favourite: Boolean)

}
