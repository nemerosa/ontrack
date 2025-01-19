export const antdDescriptionsGetCellByLabel = (locator, label) => {
    return locator
        .locator(`.ant-descriptions-item-label:has-text("${label}")`)
        .locator('..')
        .locator('.ant-descriptions-item-content')
}
