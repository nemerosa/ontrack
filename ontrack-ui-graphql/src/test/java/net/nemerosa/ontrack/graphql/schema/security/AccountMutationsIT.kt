package net.nemerosa.ontrack.graphql.schema.security

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.model.security.Account
import org.junit.Test
import kotlin.test.assertEquals

class AccountMutationsIT: AbstractQLKTITSupport() {

    @Test
    fun `Disabling an account`() {
        val account = createAccount()
        asAdmin {
            run("""
                mutation {
                    disableAccount(input: {id: ${account.id}}) {
                        account {
                            disabled
                            locked
                        }
                        errors {
                            message
                        }
                    }
                }
            """).let { data ->
                val node = assertNoUserError(data, "disableAccount")
                assertEquals(true, node.path("account").path("disabled").asBoolean())
                assertEquals(false, node.path("account").path("locked").asBoolean())
            }
        }
    }

    @Test
    fun `Enabling an account`() {
        val account = createAccount(disabled = true)
        asAdmin {
            run("""
                mutation {
                    enableAccount(input: {id: ${account.id}}) {
                        account {
                            disabled
                            locked
                        }
                        errors {
                            message
                        }
                    }
                }
            """).let { data ->
                val node = assertNoUserError(data, "enableAccount")
                assertEquals(false, node.path("account").path("disabled").asBoolean())
                assertEquals(false, node.path("account").path("locked").asBoolean())
            }
        }
    }

    @Test
    fun `Locking an account`() {
        val account = createAccount()
        asAdmin {
            run("""
                mutation {
                    lockAccount(input: {id: ${account.id}}) {
                        account {
                            disabled
                            locked
                        }
                        errors {
                            message
                        }
                    }
                }
            """).let { data ->
                val node = assertNoUserError(data, "lockAccount")
                assertEquals(false, node.path("account").path("disabled").asBoolean())
                assertEquals(true, node.path("account").path("locked").asBoolean())
            }
        }
    }

    @Test
    fun `Unlocking an account`() {
        val account = createAccount(locked = true)
        asAdmin {
            run("""
                mutation {
                    unlockAccount(input: {id: ${account.id}}) {
                        account {
                            disabled
                            locked
                        }
                        errors {
                            message
                        }
                    }
                }
            """).let { data ->
                val node = assertNoUserError(data, "unlockAccount")
                assertEquals(false, node.path("account").path("disabled").asBoolean())
                assertEquals(false, node.path("account").path("locked").asBoolean())
            }
        }
    }

    private fun createAccount(disabled: Boolean = false, locked: Boolean = false): Account = asAdmin {
        val initial = doCreateAccount()
        accountService.setAccountDisabled(initial.id, disabled)
        accountService.setAccountLocked(initial.id, locked)
        accountService.getAccount(initial.id)
    }

}