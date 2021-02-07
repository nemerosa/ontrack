package net.nemerosa.ontrack.dsl.v4.properties

import net.nemerosa.ontrack.dsl.v4.Ontrack
import net.nemerosa.ontrack.dsl.v4.ValidationStamp
import net.nemerosa.ontrack.dsl.v4.doc.DSL
import net.nemerosa.ontrack.dsl.v4.doc.DSLProperties

@DSL
@DSLProperties
class ValidationStampProperties extends ProjectEntityProperties {

    ValidationStampProperties(Ontrack ontrack, ValidationStamp validationStamp) {
        super(ontrack, validationStamp)
    }

}
