package net.nemerosa.ontrack.extension.chart

import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class DefaultChartRegistry(
    private val providers: List<ChartProvider<*, *, *>>,
) : ChartRegistry {

    private val index = providers.associateBy { it.name }

    @Suppress("UNCHECKED_CAST")
    override fun <S : Any, T : Any, C : Chart> findProvider(name: String): ChartProvider<S, T, C>? =
        index[name] as ChartProvider<S, T, C>?

    @Suppress("UNCHECKED_CAST")
    override fun <S : Any> getProvidersForSubjectClass(type: KClass<S>): List<ChartProvider<S, *, *>> =
        providers.filter {
            type.java.name == it.subjectClass.java.name
        } as List<ChartProvider<S, *, *>>
}