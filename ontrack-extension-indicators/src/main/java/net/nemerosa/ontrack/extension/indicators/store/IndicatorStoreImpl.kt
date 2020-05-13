package net.nemerosa.ontrack.extension.indicators.store

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.indicators.model.Indicator
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.repository.support.store.EntityDataStore
import net.nemerosa.ontrack.repository.support.store.EntityDataStoreFilter
import org.springframework.stereotype.Service

@Service
class IndicatorStoreImpl(
        private val entityDataStore: EntityDataStore,
        private val securityService: SecurityService
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
                    if (rep.value != null) {
                        StoredIndicator(
                                value = rep.value,
                                comment = rep.comment,
                                signature = record.signature
                        )
                    } else {
                        null
                    }
                }
    }

    override fun storeIndicator(project: Project, type: String, indicator: StoredIndicator) {
        // Gets the last version of the indicator
        val last = loadIndicator(project, type)
        if (last == null || last.value != indicator.value || last.comment != indicator.comment) {
            entityDataStore.add(
                    project,
                    STORE_CATEGORY,
                    type,
                    indicator.signature,
                    null,
                    StoredIndicatorRepresentation(
                            value = indicator.value,
                            comment = indicator.comment
                    ).asJson()
            )
        }
    }

    override fun deleteIndicator(project: Project, typeId: String): Ack {
        val existing = loadIndicator(project, typeId)
        if (existing != null) {
            entityDataStore.add(
                    project,
                    STORE_CATEGORY,
                    typeId,
                    securityService.currentSignature,
                    null,
                    StoredIndicatorRepresentation(
                            value = null,
                            comment = null
                    ).asJson()
            )
        }
        return Ack.validate(existing != null)
    }

    override fun deleteIndicatorByType(typeId: String) {
        entityDataStore.deleteByFilter(
                EntityDataStoreFilter(null)
                        .withCategory(STORE_CATEGORY)
                        .withName(typeId)
                        .withCount(Int.MAX_VALUE)
        )
    }

    companion object {
        private val STORE_CATEGORY = Indicator::class.java.name
    }

    private class StoredIndicatorRepresentation(
            val value: JsonNode?,
            val comment: String?
    )

}