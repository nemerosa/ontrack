package net.nemerosa.ontrack.extension.tfc.service

import net.nemerosa.ontrack.model.structure.Build

interface TFCBuildService {

    fun findBuild(params: TFCParameters): Build?

}