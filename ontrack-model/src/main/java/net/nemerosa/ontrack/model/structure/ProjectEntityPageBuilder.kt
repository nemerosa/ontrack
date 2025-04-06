package net.nemerosa.ontrack.model.structure

object ProjectEntityPageBuilder {

    fun getEntityPageRelativeURI(entity: ProjectEntity): String =
        when (entity.projectEntityType) {
            ProjectEntityType.PROJECT -> "project/${entity.id()}"
            ProjectEntityType.BRANCH -> "branch/${entity.id()}"
            ProjectEntityType.BUILD -> "build/${entity.id()}"
            ProjectEntityType.PROMOTION_LEVEL -> "promotionLevel/${entity.id()}"
            ProjectEntityType.PROMOTION_RUN -> "promotionRun/${entity.id()}"
            ProjectEntityType.VALIDATION_STAMP -> "validationStamp/${entity.id()}"
            ProjectEntityType.VALIDATION_RUN -> "validationRun/${entity.id()}"
        }
}
