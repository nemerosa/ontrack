package net.nemerosa.ontrack.dsl.properties

import net.nemerosa.ontrack.dsl.Ontrack
import net.nemerosa.ontrack.dsl.ValidationStamp
import net.nemerosa.ontrack.dsl.doc.DSL
import net.nemerosa.ontrack.dsl.doc.DSLProperties

@DSL
@DSLProperties
class ValidationStampProperties extends ProjectEntityProperties {

    ValidationStampProperties(Ontrack ontrack, ValidationStamp validationStamp) {
        super(ontrack, validationStamp)
    }

}
