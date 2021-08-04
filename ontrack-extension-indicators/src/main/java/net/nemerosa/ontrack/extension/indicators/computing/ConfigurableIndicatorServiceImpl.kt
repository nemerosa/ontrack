package net.nemerosa.ontrack.extension.indicators.computing

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ConfigurableIndicatorServiceImpl: ConfigurableIndicatorService {
    override fun getConfigurableIndicatorState(type: ConfigurableIndicatorType<*, *>): ConfigurableIndicatorState? {
        TODO("Not yet implemented")
    }
}