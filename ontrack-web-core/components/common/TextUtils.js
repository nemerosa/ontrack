export const conditionalPlural = (value, name) => {
    if (value > 1) {
        return `${name}s`
    } else {
        return name
    }
}