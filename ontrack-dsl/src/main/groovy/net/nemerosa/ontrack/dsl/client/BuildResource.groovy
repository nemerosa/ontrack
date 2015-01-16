package net.nemerosa.ontrack.dsl.client

import net.nemerosa.ontrack.dsl.Build
import net.nemerosa.ontrack.dsl.Ontrack
import net.nemerosa.ontrack.dsl.properties.BuildProperties
import net.nemerosa.ontrack.dsl.properties.ProjectEntityProperties

class BuildResource extends AbstractProjectResource implements Build {

    BuildResource(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    @Override
    String getProject() {
        node?.branch?.project?.name
    }

    @Override
    String getBranch() {
        node?.branch?.name
    }

    @Override
    Build promote(String promotion) {
        post(link('promote'), [
                promotionLevel: ontrack.promotionLevel(project, branch, promotion).id,
        ])
        this
    }

    @Override
    Build validate(String validationStamp, String validationStampStatus) {
        post(link('validate'), [
                validationStamp      : ontrack.validationStamp(project, branch, validationStamp).id,
                validationRunStatusId: validationStampStatus
        ])
        this
    }

    @Override
    ProjectEntityProperties getProperties() {
        new BuildProperties(ontrack, this)
    }
}
