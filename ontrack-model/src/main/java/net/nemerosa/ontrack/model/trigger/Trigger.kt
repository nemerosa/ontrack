package net.nemerosa.ontrack.model.trigger

/**
 * This interface allows the implementation of a trigger to link some data to some entity.
 *
 * The rendering of the trigger is done at client side using
 *
 * `components/framework/trigger/<id>/Component.js`
 *
 * It takes the data as JSON as a parameter:
 *
 * ```javascript
 * export default function MyTriggerComponent(data) {
 *    // ...
 * }
 * ```
 *
 * @param T Type of data for this trigger
 */
interface Trigger<T> {

    /**
     * ID of the trigger
     */
    val id: String

}