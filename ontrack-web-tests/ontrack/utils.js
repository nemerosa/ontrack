export const generate = (prefix) => {
    const root = Math.floor(Math.random() * 100000)
    return `${prefix}${root}`
}
