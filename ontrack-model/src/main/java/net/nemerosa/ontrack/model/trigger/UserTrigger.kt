package net.nemerosa.ontrack.model.trigger

/**
 * Triggered by a user.
 */
interface UserTrigger : Trigger<UserTriggerData> {

    fun createUserTriggerData(): TriggerData

}
