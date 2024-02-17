package net.nemerosa.ontrack.model.structure

interface EntityDisplayNameService {

    /**
     * Given an entity, returns its display name
     */
    fun getEntityDisplayName(entity: ProjectEntity): String

}