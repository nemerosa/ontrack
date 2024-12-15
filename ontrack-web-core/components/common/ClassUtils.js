export const actionClassName = (onClick, disabled, base = "") => {
    const addons = onClick ? (disabled ? "ot-action ot-disabled" : "ot-action") : (disabled ? "ot-disabled" : "")
    return `${base} ${addons}`
}