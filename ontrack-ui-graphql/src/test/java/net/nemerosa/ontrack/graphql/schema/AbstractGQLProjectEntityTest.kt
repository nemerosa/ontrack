package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.graphql.schema.GQLTypeCreation.Companion.getCreationFromSignature
import net.nemerosa.ontrack.model.structure.Signature.Companion.of
import org.junit.Assert
import org.junit.Test

class AbstractGQLProjectEntityTest {
    @Test
    fun signature_to_map() {
        val now = Time.now()
        val (user, time) = getCreationFromSignature(of(now, "test"))
        Assert.assertEquals("test", user)
        Assert.assertEquals(Time.forStorage(now), time)
    }

    @Test
    fun null_signature_to_map() {
        val (user, time) = getCreationFromSignature(null)
        Assert.assertNull(user)
        Assert.assertNull(time)
    }
}