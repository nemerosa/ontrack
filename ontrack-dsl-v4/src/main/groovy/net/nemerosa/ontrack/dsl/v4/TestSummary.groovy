package net.nemerosa.ontrack.dsl.v4

import groovy.transform.Canonical
import net.nemerosa.ontrack.dsl.v4.doc.DSL

@Canonical
@DSL("Test summary: passed, skipped and failed properties")
class TestSummary {
    int passed = 0
    int skipped = 0
    int failed = 0
}
