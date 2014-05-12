package net.nemerosa.ontrack.ui.support;

import net.nemerosa.ontrack.model.structure.ID;
import org.springframework.core.convert.converter.Converter;

public class IDToString implements Converter<ID, String> {
    @Override
    public String convert(ID source) {
        return source.toString();
    }
}
