package net.nemerosa.ontrack.extension.indicators.store

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.indicators.model.Indicator
import net.nemerosa.ontrack.extension.indicators.model.IndicatorStatus
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.repository.support.store.EntityDataStore
import net.nemerosa.ontrack.repository.support.store.EntityDataStoreFilter
import org.springframework.stereotype.Service

@Service
class IndicatorStoreImpl(
        private val entityDataStore: EntityDataStore
) : IndicatorStore {

    override fun loadIndicator(project: Project, type: String): StoredIndicator? {
        return entityDataStore.getByFilter(
                EntityDataStoreFilter(project)
                        .withCategory(STORE_CATEGORY)
                        .withName(type)
                        .withCount(1)
        )
                .firstOrNull()
                ?.let { record ->
                    val rep = record.data.parse<StoredIndicatorRepresentation>()
                    StoredIndicator(
                            value = rep.value,
                            status = rep.status,
                            comment = rep.comment,
                            signature = record.signature
                    )
                }
    }

    override fun storeIndicator(project: Project, type: String, indicator: StoredIndicator) {
        entityDataStore.add(
                project,
                STORE_CATEGORY,
                type,
                indicator.signature,
                null,
                StoredIndicatorRepresentation(
                        value = indicator.value,
                        status = indicator.status,
                        comment = indicator.comment
                ).asJson()
        )
    }

    companion object {
        private val STORE_CATEGORY = Indicator::class.java.name
    }

    private class StoredIndicatorRepresentation(
            val value: JsonNode,
            val status: IndicatorStatus?,
            val comment: String?
    )

}