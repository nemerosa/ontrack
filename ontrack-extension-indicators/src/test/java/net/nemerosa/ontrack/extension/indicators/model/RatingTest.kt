package net.nemerosa.ontrack.extension.indicators.model

import net.nemerosa.ontrack.extension.indicators.model.Rating.Companion.asRating
import org.junit.Test
import kotlin.test.assertEquals

class RatingTest {

    @Test
    fun conversions() {
        assertEquals(Rating.F, asRating(-1))
        assertEquals(Rating.F, asRating(0))
        assertEquals(Rating.F, asRating(1))
        assertEquals(Rating.F, asRating(10))
        assertEquals(Rating.F, asRating(20))
        assertEquals(Rating.E, asRating(30))
        assertEquals(Rating.D, asRating(40))
        assertEquals(Rating.D, asRating(50))
        assertEquals(Rating.C, asRating(60))
        assertEquals(Rating.C, asRating(70))
        assertEquals(Rating.B, asRating(80))
        assertEquals(Rating.B, asRating(90))
        assertEquals(Rating.B, asRating(99))
        assertEquals(Rating.A, asRating(100))
        assertEquals(Rating.A, asRating(101))
    }

}