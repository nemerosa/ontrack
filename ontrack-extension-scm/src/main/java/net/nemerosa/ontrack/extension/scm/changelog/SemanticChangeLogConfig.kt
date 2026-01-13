package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.model.annotations.APIDescription

interface SemanticChangeLogConfig {

    @APIDescription("Mapping types to section titles")
    val sections: List<SemanticChangeLogSection>

    @APIDescription("Types to exclude")
    val exclude: List<String>

    @APIDescription("Use emojis in the section titles")
    val emojis: Boolean

}
