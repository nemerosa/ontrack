package net.nemerosa.ontrack.dsl.v4

import groovy.transform.Canonical
import net.nemerosa.ontrack.dsl.v4.doc.DSL

@Canonical
@DSL("SonarQube measures settings parameters.")
class SonarQubeMeasuresSettings {
    List<String> measures = []
    boolean disabled = false
}
