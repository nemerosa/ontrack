package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.structure.NameDescription

interface SemanticChangeLogConfig {

    @APIDescription("Mapping types to section titles")
    val sections: List<NameDescription>

    @APIDescription("Types to exclude")
    val exclude: List<String>

}
