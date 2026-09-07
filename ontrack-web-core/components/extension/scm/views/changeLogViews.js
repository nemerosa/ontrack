import {FaListUl, FaTags} from "react-icons/fa";

/**
 * Registry of the change log views: the interchangeable renderings of the same change log.
 *
 * Views are peers on a single axis of choice — a view never offers a sub-selector for other
 * views, though it may offer controls for itself. See `docs/adr/0008-change-log-views.md`,
 * and `docs/adr/0001-branch-content-views.md` for the model this follows.
 *
 * Each entry carries:
 *
 * * `key` — stable identifier, used in the `?view=` parameter and in the user preferences
 * * `name` — label shown in the view selector
 * * `icon` — icon shown in the view selector
 *
 * Unlike the branch content views, the entries carry no `component`: both views are laid out
 * as cells of the same `GridTable`, so what changes between them is which cells are in it,
 * not which component fills a region.
 */
export const changeLogViews = [
    {
        key: 'classic',
        name: "Classic",
        icon: <FaListUl/>,
    },
    {
        key: 'semantic',
        name: "Semantic",
        icon: <FaTags/>,
    },
]

/**
 * Key of the view used when no other choice applies. Existing users keep the page they know.
 */
export const defaultChangeLogViewKey = 'classic'

/**
 * Gets the view registered under the given key, falling back to the default view when the key
 * names no known view (unknown `?view=` parameter, stale preference, nothing selected yet).
 *
 * @param key Key of the wanted view
 * @param views List of views to look into (defaults to the registry)
 */
export function getChangeLogView(key, views = changeLogViews) {
    return views.find(it => it.key === key) ??
        views.find(it => it.key === defaultChangeLogViewKey) ??
        views[0]
}
