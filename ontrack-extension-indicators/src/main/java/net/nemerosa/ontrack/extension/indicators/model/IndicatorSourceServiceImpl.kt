package net.nemerosa.ontrack.extension.indicators.model

import org.springframework.stereotype.Service

@Service
class IndicatorSourceServiceImpl(
        indicatorSourceProviders: List<IndicatorSourceProvider>
) : IndicatorSourceService {

    private val index = indicatorSourceProviders.associateBy { it.id }

    override fun findIndicatorSourceProviderById(id: String): IndicatorSourceProvider? = index[id]

}