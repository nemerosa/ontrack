package net.nemerosa.ontrack.ui.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.ConverterRegistry;

import javax.annotation.PostConstruct;

@Configuration
public class ConversionConfig {

    @Autowired
    private ConverterRegistry converterRegistry;

    @PostConstruct
    public void registerConverters() {
        converterRegistry.addConverter(new IDToString());
        converterRegistry.addConverter(new StringToID());
    }

}
