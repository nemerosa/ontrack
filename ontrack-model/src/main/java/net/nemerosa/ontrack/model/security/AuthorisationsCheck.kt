package net.nemerosa.ontrack.model.security

interface AuthorisationsCheck {

    fun isGranted(fn: Class<out GlobalFunction>): Boolean

    fun isGranted(projectId: Int, fn: Class<out ProjectFunction>): Boolean

}