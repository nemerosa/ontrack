package net.nemerosa.ontrack.extension.av.dispatcher

import net.nemerosa.ontrack.extension.general.metaInfoItem
import net.nemerosa.ontrack.extension.general.metaInfoProperty
import net.nemerosa.ontrack.extension.general.releaseProperty
import net.nemerosa.ontrack.extension.general.useLabel
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MetaInfoVersionSourceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var versionSourceFactory: VersionSourceFactory

    private lateinit var source: VersionSource

    @BeforeEach
    fun init() {
        source = versionSourceFactory.getVersionSource("metaInfo")
    }

    @Test
    fun `Build with meta information`() {
        project {
            branch {
                build {
                    val key = uid("k_")
                    val value = uid("v_")
                    metaInfoProperty(this, metaInfoItem(key, value))
                    val version = source.getVersion(this, key)
                    assertEquals(value, version)
                }
            }
        }
    }

    @Test
    fun `Build with meta information with category`() {
        project {
            branch {
                build {
                    val category = uid("c_")
                    val key = uid("k_")
                    val value = uid("v_")
                    metaInfoProperty(this, metaInfoItem(key, value, category = category))
                    val version = source.getVersion(this, "$category/$key")
                    assertEquals(value, version)
                }
            }
        }
    }

    @Test
    fun `Build with meta information with category mismatch`() {
        project {
            branch {
                build {
                    val category = uid("c_")
                    val key = uid("k_")
                    val value = uid("v_")
                    metaInfoProperty(this, metaInfoItem(key, value, category = category))
                    assertFailsWith<VersionSourceNoVersionException> {
                        source.getVersion(this, "other-category/$key")
                    }
                }
            }
        }
    }

    @Test
    fun `Build with meta information with category ok but name mismatch`() {
        project {
            branch {
                build {
                    val category = uid("c_")
                    val key = uid("k_")
                    val value = uid("v_")
                    metaInfoProperty(this, metaInfoItem(key, value, category = category))
                    assertFailsWith<VersionSourceNoVersionException> {
                        source.getVersion(this, "$category/other-key")
                    }
                }
            }
        }
    }

    @Test
    fun `Build with meta information with name mismatch`() {
        project {
            branch {
                build {
                    val key = uid("k_")
                    val value = uid("v_")
                    metaInfoProperty(this, metaInfoItem(key, value))
                    assertFailsWith<VersionSourceNoVersionException> {
                        source.getVersion(this, "other-key")
                    }
                }
            }
        }
    }

    @Test
    fun `Build with meta information without category`() {
        project {
            branch {
                build {
                    val key = uid("k_")
                    val value = uid("v_")
                    metaInfoProperty(this, metaInfoItem(key, value))
                    assertFailsWith<VersionSourceNoVersionException> {
                        source.getVersion(this, "some-category/$key")
                    }
                }
            }
        }
    }

    @Test
    fun `Build name without meta information fails`() {
        project {
            branch {
                build {
                    assertFailsWith<VersionSourceNoVersionException> {
                        source.getVersion(this, "key")
                    }
                }
            }
        }
    }

    @Test
    fun `Build label without meta information fails`() {
        project {
            useLabel(this)
            branch {
                build {
                    val label = uid("v_")
                    releaseProperty(this, label)
                    assertFailsWith<VersionSourceNoVersionException> {
                        source.getVersion(this, "key")
                    }
                }
            }
        }
    }

}