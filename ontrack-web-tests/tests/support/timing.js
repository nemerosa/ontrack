export const waitUntilCondition = async ({
                                             page,
                                             condition,
                                             timeout = 5000,
                                             interval = 500,
                                             message = "Condition not met"
                                         }) => {
    const startTime = Date.now()
    let conditionMet = false
    while ((Date.now() - startTime) < timeout && !conditionMet) {
        const conditionResult = await condition()
        if (conditionResult) {
            conditionMet = true
            break
        }
        // Wait for a short interval before retrying
        await page.waitForTimeout(interval)
    }

    if (!conditionMet) {
        throw new Error(message)
    }
}