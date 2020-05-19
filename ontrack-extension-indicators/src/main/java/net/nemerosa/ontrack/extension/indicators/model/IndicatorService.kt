package net.nemerosa.ontrack.extension.indicators.model

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.structure.Project
import java.time.Duration

interface IndicatorService {

    fun getProjectIndicators(project: Project, all: Boolean): List<Indicator<*>>

    fun <T> getProjectIndicator(project: Project, type: IndicatorType<T, *>, previous: Duration? = null): Indicator<T>

    fun getProjectIndicator(project: Project, typeId: String): Indicator<*>

    fun <T> updateProjectIndicator(project: Project, typeId: String, input: JsonNode): Indicator<T>

    fun <T> updateProjectIndicator(project: Project, type: IndicatorType<T, *>, value: T?, comment: String?): Indicator<T>

    fun deleteProjectIndicator(project: Project, typeId: String): Ack

}