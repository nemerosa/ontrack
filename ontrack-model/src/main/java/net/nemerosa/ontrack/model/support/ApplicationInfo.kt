package net.nemerosa.ontrack.model.support

data class ApplicationInfo(val type: ApplicationInfoType, val message: String) {

    companion object {
        fun success(message: String) = ApplicationInfo(ApplicationInfoType.SUCCESS, message)

        fun info(message: String) = ApplicationInfo(ApplicationInfoType.INFO, message)

        fun warning(message: String) = ApplicationInfo(ApplicationInfoType.WARNING, message)

        fun error(message: String) = ApplicationInfo(ApplicationInfoType.ERROR, message)
    }
}
