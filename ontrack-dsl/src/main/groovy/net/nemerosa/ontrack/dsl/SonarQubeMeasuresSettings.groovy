package net.nemerosa.ontrack.dsl

import groovy.transform.Canonical
import net.nemerosa.ontrack.dsl.doc.DSL

@Canonical
@DSL("SonarQube measures settings parameters.")
class SonarQubeMeasuresSettings {
    List<String> measures = []
    boolean disabled = false
}
