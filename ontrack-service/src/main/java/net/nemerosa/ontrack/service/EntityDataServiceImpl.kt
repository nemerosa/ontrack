package net.nemerosa.ontrack.service

import com.fasterxml.jackson.databind.JsonNode
import com.google.common.base.Function
import net.nemerosa.ontrack.json.ObjectMapperFactory
import net.nemerosa.ontrack.model.exceptions.JsonParsingException
import net.nemerosa.ontrack.model.security.ProjectConfig
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.EntityDataService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.repository.EntityDataRepository
import org.springframework.stereotype.Service
import java.io.IOException
import java.util.*

@Service
class EntityDataServiceImpl(
        private val repository: EntityDataRepository,
        private val securityService: SecurityService
) : EntityDataService {

    private val objectMapper = ObjectMapperFactory.create()

    override fun store(entity: ProjectEntity, key: String, value: Boolean) {
        store(entity, key, Objects.toString(value))
    }

    override fun store(entity: ProjectEntity, key: String, value: Int) {
        store(entity, key, Objects.toString(value))
    }

    override fun store(entity: ProjectEntity, key: String, value: Any) {
        securityService.checkProjectFunction(entity, ProjectConfig::class.java)
        val jsonNode = objectMapper.valueToTree<JsonNode>(value)
        repository.storeJson(entity, key, jsonNode)
    }

    override fun store(entity: ProjectEntity, key: String, value: String) {
        securityService.checkProjectFunction(entity, ProjectConfig::class.java)
        repository.store(entity, key, value)
    }

    override fun retrieveBoolean(entity: ProjectEntity, key: String): Optional<Boolean> {
        return retrieve(entity, key) { it == "true" }
    }

    override fun retrieveInteger(entity: ProjectEntity, key: String): Optional<Int> {
        return retrieve(entity, key) { value -> Integer.parseInt(value, 10) }
    }

    override fun retrieve(entity: ProjectEntity, key: String): Optional<String> {
        return retrieve(entity, key) { it }
    }

    override fun retrieveJson(entity: ProjectEntity, key: String): Optional<JsonNode> {
        return retrieve(entity, key) { value ->
            try {
                objectMapper.readTree(value)
            } catch (e: IOException) {
                throw JsonParsingException(e)
            }
        }
    }

    override fun <T> retrieve(entity: ProjectEntity, key: String, type: Class<T>): Optional<T> {
        return retrieve<T>(entity, key) { value ->
            try {
                objectMapper.readValue<T>(value, type)
            } catch (e: IOException) {
                throw JsonParsingException(e)
            }
        }
    }

    protected fun <T> retrieve(entity: ProjectEntity, key: String, parser: (String) -> T): Optional<T> {
        securityService.checkProjectFunction(entity, ProjectView::class.java)
        return repository.retrieve(entity, key).map { parser(it) }
    }

    override fun delete(entity: ProjectEntity, key: String) {
        securityService.checkProjectFunction(entity, ProjectConfig::class.java)
        repository.delete(entity, key)
    }

    override fun <T> withData(entity: ProjectEntity, key: String, type: Class<T>, processFn: Function<T, T>) {
        retrieve(entity, key, type).ifPresent { data -> store(entity, key, processFn.apply(data) as Any) }
    }

    override fun findFirstJsonFieldGreaterOrEqual(type: ProjectEntityType, reference: Pair<String, Int>, value: Long, vararg jsonPath: String): Int? {
        return repository.findFirstJsonFieldGreaterOrEqual(type, reference, value, *jsonPath)
    }
}
