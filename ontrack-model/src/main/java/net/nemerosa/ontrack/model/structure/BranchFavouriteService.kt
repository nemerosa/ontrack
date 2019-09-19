package net.nemerosa.ontrack.model.structure

interface BranchFavouriteService {

    /**
     * Is this branch a favourite?
     */
    fun isBranchFavourite(branch: Branch): Boolean

    /**
     * Sets a branch as favourite (or not)
     */
    fun setBranchFavourite(branch: Branch, favourite: Boolean)

}
