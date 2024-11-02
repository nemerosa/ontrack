import {test} from "@playwright/test";
import {ontrack} from "@ontrack/ontrack";
import {login} from "../../core/login";
import {generate} from "@ontrack/utils";
import {createSlot} from "./slotFixtures";
import {SlotPage} from "./SlotPage";

test('adding a branch pattern admission rule with default name', async ({page}) => {
    // Provisioning
    const {slot} = await createSlot(ontrack())

    // Login
    await login(page)
    // Going to the slot page
    const slotPage = new SlotPage(page, slot)
    await slotPage.goTo()

    // Adding an admission rule
    const description = generate("rule-")
    await slotPage.addAdmissionRule({
        rule: "Branch pattern",
        name: "branchPattern",
        description: description,
        config: () => {
            const includes = page.getByLabel('Includes regular expressions');
            includes.click()
            includes.fill('release-.*')
            includes.press('Enter')
        }
    })
})

test('adding a manual approval admission rule with default name', async ({page}) => {
    // Provisioning
    const {slot} = await createSlot(ontrack())

    // Login
    await login(page)
    // Going to the slot page
    const slotPage = new SlotPage(page, slot)
    await slotPage.goTo()

    // Adding an admission rule
    const description = generate("rule-")
    await slotPage.addAdmissionRule({
        rule: "Manual approval",
        name: "manual",
        description: description,
        config: () => {
            page.getByLabel('Message').fill("Approval message")
        }
    })
})