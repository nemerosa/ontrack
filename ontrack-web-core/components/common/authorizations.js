export function isAuthorized(context, domain, action) {
    if (context.authorizations) {
        const authorization = context.authorizations.find(it => it.name === domain && it.action === action)
        return authorization && authorization.authorized
    } else {
        return false
    }
}