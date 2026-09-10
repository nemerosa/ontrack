/**
 * The bottom navigation bar's tabs.
 *
 * A phone app is defined as much by what it leaves out as by what it shows.
 * Three tabs, all inside `/mobile`: leaving that prefix would drop the user into
 * the desktop UI and, once the app is installed as a PWA scoped to `/mobile`,
 * out of the app itself.
 *
 * Deliberately not shared with the desktop `NavBar` - see
 * `doc/dev-guide/ui/mobile-ui.md` on why the layouts stay separate.
 */
import {FaHome, FaSearch, FaSitemap} from "react-icons/fa"
import {MOBILE_PREFIX, MOBILE_PROJECTS} from "@components/mobile/mobileRoutes"

export const MOBILE_NAV_ITEMS = [
    {key: 'home', href: MOBILE_PREFIX, label: "Home", icon: <FaHome/>},
    {key: 'projects', href: MOBILE_PROJECTS, label: "Projects", icon: <FaSitemap/>},
    {key: 'search', href: `${MOBILE_PREFIX}/search`, label: "Search", icon: <FaSearch/>},
]

/**
 * Which tab a screen belongs to.
 *
 * Matched on the longest `href` rather than the first: `/mobile` is a prefix of
 * every mobile path, so a first-match scan would keep Home lit everywhere.
 *
 * @param {string} pathname
 * @returns {string|null} The key of the tab to light, or `null` for a screen
 *   that belongs to no tab - the interstitial, for one.
 */
export function activeMobileNavKey(pathname) {
    if (typeof pathname !== 'string') return null
    const match = MOBILE_NAV_ITEMS
        .filter(item => pathname === item.href || (
            // Home's href *is* the mobile root, so a subtree match would light
            // it on every screen. It only ever matches its own path.
            item.href !== MOBILE_PREFIX && pathname.startsWith(`${item.href}/`)
        ))
        .sort((a, b) => b.href.length - a.href.length)[0]
    return match ? match.key : null
}
