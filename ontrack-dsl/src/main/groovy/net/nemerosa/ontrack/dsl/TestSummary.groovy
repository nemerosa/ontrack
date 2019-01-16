package net.nemerosa.ontrack.dsl

import groovy.transform.Canonical
import net.nemerosa.ontrack.dsl.doc.DSL

@Canonical
@DSL("Test summary: passed, skipped and failed properties")
class TestSummary {
    int passed = 0
    int skipped = 0
    int failed = 0
}
