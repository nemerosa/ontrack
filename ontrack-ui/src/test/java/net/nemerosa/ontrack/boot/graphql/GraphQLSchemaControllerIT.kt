package net.nemerosa.ontrack.boot.graphql

import net.nemerosa.ontrack.boot.ui.AbstractWebTestSupport
import net.nemerosa.ontrack.graphql.GraphQLSchemaController
import net.nemerosa.ontrack.json.format
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import java.io.File

class GraphQLSchemaControllerIT: AbstractWebTestSupport() {

    @Autowired
    private lateinit var controller: GraphQLSchemaController

    @Test
    fun `JSON schema`() {
        val json = controller.schemaAsJson()
        File("build/graphql.json").writeText(
                json.format()
        )
    }

}