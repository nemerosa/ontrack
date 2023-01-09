package net.nemerosa.ontrack.ui.support

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.ConverterRegistry
import javax.annotation.PostConstruct

@Configuration
class ConversionConfig(
        private val converterRegistry: ConverterRegistry,
) {

    private val logger: Logger = LoggerFactory.getLogger(ConversionConfig::class.java)

    @PostConstruct
    fun registerConverters() {
        logger.info("Registering ID/String Spring converters")
        converterRegistry.addConverter(IDToString())
        converterRegistry.addConverter(StringToID())
    }
}