package net.nemerosa.ontrack.model.links

enum class BranchLinksDirection(
    val id: String,
    val text: String
) {

    USING("using", "Using"),
    USED_BY("usedBy", "Used by"),

}