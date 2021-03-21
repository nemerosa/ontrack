package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ReleasePropertyMutationProviderIT: AbstractQLKTITSupport() {

    @Test
    fun `Setting the release property on a build by ID and then removing it`() {
        asAdmin {
            project {
                branch {
                    build {
                        run("""
                            mutation {
                                setBuildReleasePropertyById(input: {
                                    id: $id,
                                    release: "RC-1"
                                }) {
                                    errors {
                                        message
                                    }
                                }
                            }
                        """).let { data ->
                            assertNoUserError(data, "setBuildReleasePropertyById")
                            assertNotNull(getProperty(this, ReleasePropertyType::class.java)) { property ->
                                assertEquals("RC-1", property.name)
                            }
                        }
                        run("""
                            mutation {
                                deleteBuildReleasePropertyById(input: {
                                    id: $id
                                }) {
                                    errors {
                                        message
                                    }
                                }
                            }
                        """).let { data ->
                            assertNoUserError(data, "deleteBuildReleasePropertyById")
                            assertNull(getProperty(this, ReleasePropertyType::class.java))
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Setting the release property on a build by name and then removing it`() {
        asAdmin {
            project {
                branch {
                    build {
                        run("""
                            mutation {
                                setBuildReleaseProperty(input: {
                                    project: "${project.name}",
                                    branch: "${branch.name}",
                                    build: "$name",
                                    release: "RC-1"
                                }) {
                                    errors {
                                        message
                                    }
                                }
                            }
                        """).let { data ->
                            assertNoUserError(data, "setBuildReleaseProperty")
                            assertNotNull(getProperty(this, ReleasePropertyType::class.java)) { property ->
                                assertEquals("RC-1", property.name)
                            }
                        }
                        run("""
                            mutation {
                                deleteBuildReleaseProperty(input: {
                                    project: "${project.name}",
                                    branch: "${branch.name}",
                                    build: "$name"
                                }) {
                                    errors {
                                        message
                                    }
                                }
                            }
                        """).let { data ->
                            assertNoUserError(data, "deleteBuildReleaseProperty")
                            assertNull(getProperty(this, ReleasePropertyType::class.java))
                        }
                    }
                }
            }
        }
    }

}