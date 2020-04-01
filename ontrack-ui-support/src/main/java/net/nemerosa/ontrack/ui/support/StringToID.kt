package net.nemerosa.ontrack.ui.support

import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.ID.Companion.of
import org.apache.commons.lang3.StringUtils
import org.springframework.core.convert.converter.Converter

class StringToID : Converter<String, ID> {
    override fun convert(source: String): ID {
        return if (StringUtils.isNumeric(source)) {
            of(source.toInt(10))
        } else {
            ID.NONE
        }
    }
}