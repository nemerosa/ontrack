package net.nemerosa.ontrack.extension.general.templating

import net.nemerosa.ontrack.model.annotations.APIDescription

data class BuildLinkTemplatingSourceDocumentation(
    @APIDescription("Name of the project to get a link to.")
    val project: String,
    @APIDescription("Qualifier of the link (optional).")
    val qualifier: String? = "",
    @APIDescription(
        """
            How to the linked build must be rendered.
            
            - name: build name only
            - release: build release/version/label (required in this case)
            - auto: build release/version/label if available, build name otherwise
        """
    )
    val mode: BuildLinkTemplatingSourceMode = BuildLinkTemplatingSourceMode.NAME,
)
