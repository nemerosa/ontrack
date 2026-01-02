package net.nemerosa.ontrack.model.structure


class SyncResult {
    var unknownTargetIgnored: Int = 0
    var unknownTargetDeleted: Int = 0
    var created: Int = 0
    var presentTargetIgnored: Int = 0
    var presentTargetReplaced: Int = 0

    companion object {
        @JvmStatic
        fun empty() = SyncResult()
    }

    fun create() {
        created++
    }

    fun ignorePresentTarget() {
        presentTargetIgnored++
    }

    fun replacePresentTarget() {
        presentTargetReplaced++
    }

}
