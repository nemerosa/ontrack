package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.structure.StandardBuildFilterData
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals

class StandardBuildFilterDataTest {
    @Test
    fun json_to_afterDate() {
        assertEquals(
            StandardBuildFilterData.of(2).withAfterDate(LocalDate.of(2014, 7, 14)).asJson(),
            mapOf(
                "count" to 2,
                "afterDate" to "2014-07-14",
            ).asJson()
        )
    }
}