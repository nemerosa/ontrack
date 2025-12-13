import {expect} from "@playwright/test";

export const checkListContainsItemText = async (list, item) => {
    const match = list.locator('.ant-list-items', {hasText: item})
    await expect(match).toHaveCount(1)
}