package net.nemerosa.ontrack.extension.indicators.model

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.structure.Project
import java.time.Duration
import java.time.LocalDateTime

interface IndicatorService {

    fun getProjectIndicators(project: Project, all: Boolean): List<Indicator<*>>

    fun <T> getProjectIndicator(project: Project, type: IndicatorType<T, *>, previous: Duration? = null): Indicator<T>

    /**
     * Gets the indicator which was previously entered for this [project] and this [type].
     *
     * The returned value is never `null` but its [value][Indicator.value] might be.
     */
    fun <T> getPreviousProjectIndicator(project: Project, type: IndicatorType<T, *>): Indicator<T>

    fun getProjectIndicator(project: Project, typeId: String): Indicator<*>

    fun <T> updateProjectIndicator(project: Project, typeId: String, input: JsonNode): Indicator<T>

    /**
     * @param time Time to set for the indicator (used only for tests)
     */
    fun <T> updateProjectIndicator(project: Project, type: IndicatorType<T, *>, value: T?, comment: String?, time: LocalDateTime? = null): Indicator<T>

    fun deleteProjectIndicator(project: Project, typeId: String): Ack

}