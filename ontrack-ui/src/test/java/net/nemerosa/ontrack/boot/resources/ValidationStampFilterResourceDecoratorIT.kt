package net.nemerosa.ontrack.boot.resources

import net.nemerosa.ontrack.model.security.Roles
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.ValidationStampFilter
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

/**
 * Testing the update, delete, share to project, share to global, links when
 * using the [ValidationStampFilterResourceDecorator].
 *
 * The tests are a matrix on following axes:
 *
 * * scope: branch, project, global
 * * role: view only, participant, validation stamp manager, project manager, project owner, global config
 */
class ValidationStampFilterResourceDecoratorIT : AbstractResourceDecoratorTestSupport() {

    @Autowired
    private lateinit var decorator: ValidationStampFilterResourceDecorator

    @Test
    fun `Branch filter for view only`() {
        project {
            branch {
                asUserWithView(this) {
                    filter(branch = this).decorate(decorator) {
                        assertLinkNotPresent("_update")
                        assertLinkNotPresent("_delete")
                        assertLinkNotPresent("_shareAtProject")
                        assertLinkNotPresent("_shareAtGlobal")
                    }
                }
            }
        }
    }

    @Test
    fun `Branch filter for participant`() {
        project {
            branch {
                asAccountWithProjectRole(Roles.PROJECT_PARTICIPANT) {
                    filter(branch = this).decorate(decorator) {
                        assertLinkPresent("_update")
                        assertLinkPresent("_delete")
                        assertLinkNotPresent("_shareAtProject")
                        assertLinkNotPresent("_shareAtGlobal")
                    }
                }
            }
        }
    }

    @Test
    fun `Branch filter for validation stamp manager`() {
        project {
            branch {
                asAccountWithProjectRole(Roles.PROJECT_VALIDATION_MANAGER) {
                    filter(branch = this).decorate(decorator) {
                        assertLinkPresent("_update")
                        assertLinkPresent("_delete")
                        assertLinkPresent("_shareAtProject")
                        assertLinkNotPresent("_shareAtGlobal")
                    }
                }
            }
        }
    }

    @Test
    fun `Branch filter for project manager`() {
        project {
            branch {
                asAccountWithProjectRole(Roles.PROJECT_MANAGER) {
                    filter(branch = this).decorate(decorator) {
                        assertLinkPresent("_update")
                        assertLinkPresent("_delete")
                        assertLinkPresent("_shareAtProject")
                        assertLinkNotPresent("_shareAtGlobal")
                    }
                }
            }
        }
    }

    @Test
    fun `Branch filter for project owner`() {
        project {
            branch {
                asAccountWithProjectRole(Roles.PROJECT_OWNER) {
                    filter(branch = this).decorate(decorator) {
                        assertLinkPresent("_update")
                        assertLinkPresent("_delete")
                        assertLinkPresent("_shareAtProject")
                        assertLinkNotPresent("_shareAtGlobal")
                    }
                }
            }
        }
    }

    @Test
    fun `Branch filter for admin`() {
        project {
            branch {
                asAccountWithGlobalRole(Roles.GLOBAL_ADMINISTRATOR) {
                    filter(branch = this).decorate(decorator) {
                        assertLinkPresent("_update")
                        assertLinkPresent("_delete")
                        assertLinkPresent("_shareAtProject")
                        assertLinkPresent("_shareAtGlobal")
                    }
                }
            }
        }
    }

    @Test
    fun `Project filter for view only`() {
        project {
            asUserWithView(this) {
                filter(project = this).decorate(decorator) {
                    assertLinkNotPresent("_update")
                    assertLinkNotPresent("_delete")
                    assertLinkNotPresent("_shareAtProject")
                    assertLinkNotPresent("_shareAtGlobal")
                }
            }
        }
    }

    @Test
    fun `Project filter for participant`() {
        project {
            asAccountWithProjectRole(Roles.PROJECT_PARTICIPANT) {
                filter(project = this).decorate(decorator) {
                    assertLinkNotPresent("_update")
                    assertLinkNotPresent("_delete")
                    assertLinkNotPresent("_shareAtProject")
                    assertLinkNotPresent("_shareAtGlobal")
                }
            }
        }
    }

    @Test
    fun `Project filter for validation stamp manager`() {
        project {
            asAccountWithProjectRole(Roles.PROJECT_VALIDATION_MANAGER) {
                filter(project = this).decorate(decorator) {
                    assertLinkPresent("_update")
                    assertLinkPresent("_delete")
                    assertLinkNotPresent("_shareAtProject")
                    assertLinkNotPresent("_shareAtGlobal")
                }
            }
        }
    }

    @Test
    fun `Project filter for project manager`() {
        project {
            asAccountWithProjectRole(Roles.PROJECT_MANAGER) {
                filter(project = this).decorate(decorator) {
                    assertLinkPresent("_update")
                    assertLinkPresent("_delete")
                    assertLinkNotPresent("_shareAtProject")
                    assertLinkNotPresent("_shareAtGlobal")
                }
            }
        }
    }

    @Test
    fun `Project filter for project owner`() {
        project {
            asAccountWithProjectRole(Roles.PROJECT_OWNER) {
                filter(project = this).decorate(decorator) {
                    assertLinkPresent("_update")
                    assertLinkPresent("_delete")
                    assertLinkNotPresent("_shareAtProject")
                    assertLinkNotPresent("_shareAtGlobal")
                }
            }
        }
    }

    @Test
    fun `Project filter for admin`() {
        project {
            asAccountWithGlobalRole(Roles.GLOBAL_ADMINISTRATOR) {
                filter(project = this).decorate(decorator) {
                    assertLinkPresent("_update")
                    assertLinkPresent("_delete")
                    assertLinkNotPresent("_shareAtProject")
                    assertLinkPresent("_shareAtGlobal")
                }
            }
        }
    }

    @Test
    fun `Global filter for view only`() {
        project {
            asUserWithView(this) {
                filter().decorate(decorator) {
                    assertLinkNotPresent("_update")
                    assertLinkNotPresent("_delete")
                    assertLinkNotPresent("_shareAtProject")
                    assertLinkNotPresent("_shareAtGlobal")
                }
            }
        }
    }

    @Test
    fun `Global filter for participant`() {
        project {
            asAccountWithProjectRole(Roles.PROJECT_PARTICIPANT) {
                filter().decorate(decorator) {
                    assertLinkNotPresent("_update")
                    assertLinkNotPresent("_delete")
                    assertLinkNotPresent("_shareAtProject")
                    assertLinkNotPresent("_shareAtGlobal")
                }
            }
        }
    }

    @Test
    fun `Global filter for validation stamp manager`() {
        project {
            asAccountWithProjectRole(Roles.PROJECT_VALIDATION_MANAGER) {
                filter().decorate(decorator) {
                    assertLinkNotPresent("_update")
                    assertLinkNotPresent("_delete")
                    assertLinkNotPresent("_shareAtProject")
                    assertLinkNotPresent("_shareAtGlobal")
                }
            }
        }
    }

    @Test
    fun `Global filter for project manager`() {
        project {
            asAccountWithProjectRole(Roles.PROJECT_MANAGER) {
                filter().decorate(decorator) {
                    assertLinkNotPresent("_update")
                    assertLinkNotPresent("_delete")
                    assertLinkNotPresent("_shareAtProject")
                    assertLinkNotPresent("_shareAtGlobal")
                }
            }
        }
    }

    @Test
    fun `Global filter for project owner`() {
        project {
            asAccountWithProjectRole(Roles.PROJECT_OWNER) {
                filter().decorate(decorator) {
                    assertLinkNotPresent("_update")
                    assertLinkNotPresent("_delete")
                    assertLinkNotPresent("_shareAtProject")
                    assertLinkNotPresent("_shareAtGlobal")
                }
            }
        }
    }

    @Test
    fun `Global filter for admin`() {
        project {
            asAccountWithGlobalRole(Roles.GLOBAL_ADMINISTRATOR) {
                filter().decorate(decorator) {
                    assertLinkPresent("_update")
                    assertLinkPresent("_delete")
                    assertLinkNotPresent("_shareAtProject")
                    assertLinkNotPresent("_shareAtGlobal")
                }
            }
        }
    }

    private fun filter(project: Project? = null, branch: Branch? = null) =
            ValidationStampFilter(
                    name = uid("F"),
                    project = project,
                    branch = branch,
                    vsNames = listOf("VS")
            )

}