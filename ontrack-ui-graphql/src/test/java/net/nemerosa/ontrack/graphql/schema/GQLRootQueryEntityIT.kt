package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.json.getJsonField
import net.nemerosa.ontrack.json.getRequiredIntField
import net.nemerosa.ontrack.json.getRequiredTextField
import net.nemerosa.ontrack.test.assertJsonNotNull
import net.nemerosa.ontrack.test.assertJsonNull
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class GQLRootQueryEntityIT : AbstractQLKTITSupport() {

    @Test
    fun `Entity not found`() {
        val project = project()
        asAdmin {
            structureService.deleteProject(project.id)
            run("""{
                entity(type: PROJECT, id: ${project.id}) {
                    entity {
                        id
                    }
                }
            }""") { data ->
                assertJsonNull(data.getJsonField("entity"))
            }
        }
    }

    @Test
    fun `Project entity`() {
        asAdmin {
            project {
                run("""{
                    entity(type: PROJECT, id: $id) {
                        type
                        id
                        name
                        entityName
                        entity {
                            id
                        }
                    }
                }""") { data ->
                    assertJsonNotNull(data.getJsonField("entity"), "Entity has been found") {
                        assertEquals("PROJECT", getRequiredTextField("type"))
                        assertEquals(project.id(), getRequiredIntField("id"))
                        assertEquals(project.name, getRequiredTextField("name"))
                        assertEquals(project.entityDisplayName, getRequiredTextField("entityName"))
                        assertEquals(project.id(), getJsonField("entity")?.getRequiredIntField("id"))
                    }
                }
            }
        }
    }

    @Test
    fun `Branch entity`() {
        asAdmin {
            project {
                branch branch@{
                    run("""{
                            entity(type: BRANCH, id: $id) {
                                type
                                id
                                name
                                entityName
                                entity {
                                    id
                                }
                            }
                        }""") { data ->
                        assertJsonNotNull(data.getJsonField("entity"), "Entity has been found") {
                            assertEquals("BRANCH", getRequiredTextField("type"))
                            assertEquals(this@branch.id(), getRequiredIntField("id"))
                            assertEquals(this@branch.name, getRequiredTextField("name"))
                            assertEquals(this@branch.entityDisplayName, getRequiredTextField("entityName"))
                            assertEquals(this@branch.id(), getJsonField("entity")?.getRequiredIntField("id"))
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Build entity`() {
        asAdmin {
            project {
                branch {
                    build build@{
                        run("""{
                            entity(type: BUILD, id: $id) {
                                type
                                id
                                name
                                entityName
                                entity {
                                    id
                                }
                            }
                        }""") { data ->
                            assertJsonNotNull(data.getJsonField("entity"), "Entity has been found") {
                                assertEquals("BUILD", getRequiredTextField("type"))
                                assertEquals(this@build.id(), getRequiredIntField("id"))
                                assertEquals(this@build.name, getRequiredTextField("name"))
                                assertEquals(this@build.entityDisplayName, getRequiredTextField("entityName"))
                                assertEquals(this@build.id(), getJsonField("entity")?.getRequiredIntField("id"))
                            }
                        }
                    }
                }
            }
        }
    }

}