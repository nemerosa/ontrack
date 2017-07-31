package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.structure.ValidationDataType
import net.nemerosa.ontrack.model.structure.ValidationDataTypeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ValidationDataTypeServiceImpl
@Autowired
constructor(
        private val types: List<ValidationDataType<*, *>>
) : ValidationDataTypeService {

    override fun getAllTypes(): List<ValidationDataType<*, *>> = types
}
