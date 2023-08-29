package net.nemerosa.ontrack.model.structure

data class BuildLinkFormItem(
        val project: String,
        val build: String,
        val qualifier: String = BuildLink.DEFAULT,
)
