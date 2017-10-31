package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.extension.Extension
import net.nemerosa.ontrack.model.form.Form

/**
 * Definition and validation of some arbitrary data associated with [ValidationStamp]s
 * and [ValidationRun]s.
 *
 * @param C Configuration data associated with this type.
 * @param T Type of data associated with this type.
 */
interface ValidationDataType<C, T> : Extension {

    /**
     * Display name for this type
     */
    val displayName: String

    /**
     * Translates the configuration data into JSON
     */
    fun configToJson(config: C): JsonNode

    /**
     * Translates some JSON into configuration data
     * @return The data or `null` if the JSON cannot be translated. The implementation can also choose
     * to throw an exception instead.
     */
    fun configFromJson(node: JsonNode?): C?

    /**
     * Gets a form to edit / create the configuration.
     *
     * @param config Data to edit - `null` if the form is for a creation
     * @return A prefilled form or [Form.create] if not editable (no config)
     */
    fun getConfigForm(config: C?): Form

    /**
     * Creates the config object from the JSON returned by a form edition
     *
     * @param node JSON returned by the form edition
     * @return Configuration
     */
    fun fromConfigForm(node: JsonNode?): C?

    /**
     * Translates the data into JSON
     */
    fun toJson(data: T): JsonNode

    /**
     * Translates some JSON into data
     * @return The data or `null` if the JSON cannot be translated. The implementation can also choose
     * to throw an exception instead.
     */
    fun fromJson(node: JsonNode): T?

    /**
     * Gets a form to edit / create some data.
     *
     * @param data Data to edit - `null` if the form is for a creation
     * @return A prefilled form
     */
    fun getForm(data: T?): Form

    /**
     * Creates the data object from the JSON returned by a form edition
     *
     * @param node JSON returned by the form edition
     * @return Data
     */
    fun fromForm(node: JsonNode?): T?

    /**
     * Computes the status from some data
     *
     * @param config Configuration to use
     * @param data Data associated with a run
     * @return Status or `null` if no status can be determined
     */
    fun computeStatus(config: C?, data: T): ValidationRunStatusID?

    /**
     * Validates some data according to its type and configuration.
     *
     * @param config Configuration associated to the type
     * @param data Data to validate
     * @return Validated data
     */
    fun validateData(config: C?, data: T?): T
}
