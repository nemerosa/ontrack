package net.nemerosa.ontrack.model.structure;

data class BuildLinkForm(
        val addOnly: Boolean,
        val links: List<BuildLinkFormItem>
) {
    constructor(addOnly: Boolean = false, vararg links: BuildLinkFormItem) : this(
            addOnly,
            links.asList()
    )
}