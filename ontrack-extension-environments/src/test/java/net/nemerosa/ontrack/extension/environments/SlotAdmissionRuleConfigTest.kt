package net.nemerosa.ontrack.extension.environments

import net.nemerosa.ontrack.extension.environments.SlotAdmissionRuleConfig.Companion.checkRuleName
import net.nemerosa.ontrack.extension.environments.service.SlotAdmissionRuleConfigNameFormatException
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class SlotAdmissionRuleConfigTest {

    @Test
    fun `Checking names`() {
        assertFailsWith<SlotAdmissionRuleConfigNameFormatException> {
            checkRuleName("")
        }
        assertFailsWith<SlotAdmissionRuleConfigNameFormatException> {
            checkRuleName(" ")
        }
        assertFailsWith<SlotAdmissionRuleConfigNameFormatException> {
            checkRuleName("Not a good name")
        }
        checkRuleName("This-is-a-good-name")
        checkRuleName("thisIsABetterName")
        checkRuleName("thisIsAlsoAGoodName")
    }

}