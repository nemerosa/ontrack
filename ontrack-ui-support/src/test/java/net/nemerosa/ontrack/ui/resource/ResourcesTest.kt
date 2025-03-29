package net.nemerosa.ontrack.ui.resource

import net.nemerosa.ontrack.json.ObjectMapperFactory.create
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Branch.Companion.of
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.Project.Companion.of
import net.nemerosa.ontrack.model.structure.TestFixtures
import net.nemerosa.ontrack.model.structure.TestFixtures.SIGNATURE_OBJECT
import org.junit.jupiter.api.Test
import java.net.URI
import java.util.*

class ResourcesTest : AbstractResourceTest() {

    @Test
    fun to_json() {
        val collection = Resources.of(
            listOf(
                Dummy("1"),
                Dummy("2")
            ),
            URI.create("http://host/dummy")
        )
        assertResourceJson(
            mapper,
            mapOf(
                "_self" to "http://host/dummy",
                "resources" to listOf(
                    mapOf(
                        "version" to "1"
                    ),
                    mapOf(
                        "version" to "2"
                    )
                )
            ).asJson(),
            create().valueToTree(collection)
        )
    }

    @Test
    fun resource_collection_with_filtering() {
        val project = of(NameDescription("PRJ", "Project"))
        val branches = Arrays.asList<Branch>(
            of(project, NameDescription("B1", "Branch 1")).withSignature(TestFixtures.SIGNATURE),
            of(project, NameDescription("B2", "Branch 2")).withSignature(TestFixtures.SIGNATURE)
        )
        val resourceCollection = Resources.of(
            branches,
            URI.create("urn:branch")
        )

        assertResourceJson(
            mapper,
            mapOf(
                "_self" to "urn:branch",
                "resources" to listOf(
                    mapOf(
                        "id" to 0,
                        "name" to "B1",
                        "description" to "Branch 1",
                        "disabled" to false,
                        "signature" to SIGNATURE_OBJECT
                    ),
                    mapOf(
                        "id" to 0,
                        "name" to "B2",
                        "description" to "Branch 2",
                        "disabled" to false,
                        "signature" to SIGNATURE_OBJECT,
                    )
                )
            ).asJson(),
            resourceCollection,
            Resources::class.java
        )
    }
}
