package net.nemerosa.ontrack.ui.support

import net.nemerosa.ontrack.model.structure.ID
import org.springframework.core.convert.converter.Converter

class IDToString : Converter<ID, String> {
    override fun convert(source: ID): String {
        return source.toString()
    }
}