package net.nemerosa.ontrack.extension.av.audit

import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.json.parseAsJson
import org.junit.jupiter.api.Test

class AutoVersioningAuditEntryStateTest {

    @Test
    fun `Parsing from JSON`() {
        val json = """
            [{"data":{"branch":"branch","prLink":"uri:1","prName":"#1"},"state":"PR_MERGED","signature":{"time":"2025-11-21T17:28:06.945742Z","user":{"name":"admin-28066291@ontrack.local"}}},{"data":{},"state":"RECEIVED","signature":{"time":"2025-11-21T17:28:06.944283Z","user":{"name":"admin-28066291@ontrack.local"}}},{"data":{},"state":"SCHEDULED","signature":{"time":"2025-11-21T17:28:06.943137Z","user":{"name":"admin-28066291@ontrack.local"}}},{"data":{},"state":"CREATED","signature":{"time":"2025-11-21T17:28:06.941236Z","user":{"name":"admin-28066291@ontrack.local"}}}]
        """.trimIndent().parseAsJson()
        val states = json.parse<List<AutoVersioningAuditEntryState>>()
        println(states)
    }

}