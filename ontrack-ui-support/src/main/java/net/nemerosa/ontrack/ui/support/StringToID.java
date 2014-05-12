package net.nemerosa.ontrack.ui.support;

import net.nemerosa.ontrack.model.structure.ID;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;

public class StringToID implements Converter<String, ID> {
    @Override
    public ID convert(String source) {
        if (StringUtils.isNumeric(source)) {
            return ID.of(Integer.parseInt(source, 10));
        } else {
            return ID.NONE;
        }
    }
}
