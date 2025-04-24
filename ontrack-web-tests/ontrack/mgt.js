/**
 * Management interface of Ontrack
 */
export const mgtAccountToken = async (username, mgt) => {
    console.log(`Getting API token for user ${username}`)
    const response = await fetch(
        `${mgt}/manage/account/${username}`,
    )

    if (response.ok) {
        return await response.text()
    } else {
        throw new Error(`Cannot get the account token for user ${username}`)
    }
}