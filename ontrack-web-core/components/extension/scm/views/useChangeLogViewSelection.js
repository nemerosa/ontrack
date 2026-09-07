import {useCallback, useRef} from "react";
import {useRouter} from "next/router";
import {usePreferences} from "@components/providers/PreferencesProvider";
import {changeLogViews, getChangeLogView} from "@components/extension/scm/views/changeLogViews";
import {
    formatSemanticOptionParam,
    parseSemanticOptionParam,
    semanticChangeLogOptions,
} from "@components/extension/scm/views/semanticChangeLogOptions";

/**
 * Name of the query parameter naming the selected change log view.
 */
export const changeLogViewParam = 'view'

/**
 * Selection of the change log view and of the semantic view's options.
 *
 * The `?view=` parameter is authoritative and stays in the URL, so that any choice is
 * linkable; the same goes for the four semantic options. In the absence of a parameter, the
 * user's own preferences apply, and in the absence of those, the defaults.
 *
 * Parameters are written **only on user action**, never on load: a bare change log link keeps
 * meaning "however *you* like to read it", while a link shared after picking options carries
 * them. See `docs/adr/0008-change-log-views.md`.
 *
 * @param views List of available views (defaults to the registry)
 */
export default function useChangeLogViewSelection({views = changeLogViews} = {}) {

    const router = useRouter()
    const preferences = usePreferences()
    const {selectedChangeLogViewKey, setPreferences} = preferences

    const selectedViewKey = getChangeLogView(
        router.query?.[changeLogViewParam] ?? selectedChangeLogViewKey,
        views,
    )?.key

    // Parameter, then preference, then default — the same order as the view key above.
    const options = Object.fromEntries(
        semanticChangeLogOptions.map(option => [
            option.name,
            parseSemanticOptionParam(option, router.query?.[option.param])
                ?? preferences[option.preference]
                ?? option.defaultValue,
        ])
    )

    // `useRouter` hands out a fresh copy of the query on each render, so a callback captured by
    // a component which does not rebuild it on every render — the command bar does exactly that
    // — would write back the query of the render it was captured in, losing anything set since.
    // The callbacks are therefore given a stable identity and read what they need at call time.
    const latest = useRef(null)
    latest.current = {views, router, selectedChangeLogViewKey, preferences, setPreferences}

    // Patches already sent to the router but not yet visible in its query. `router.replace` is
    // asynchronous, so two clicks inside one update window - flipping two switches in a row,
    // which the four in the panel header invite - would both build on the pre-patch query and
    // the first change would be dropped from the URL, snapping its switch back.
    const pending = useRef({})

    const replaceQuery = useCallback((patch) => {
        const {router} = latest.current
        // Whatever the router has caught up with is no longer pending
        Object.keys(pending.current).forEach(key => {
            if (router.query[key] === pending.current[key]) {
                delete pending.current[key]
            }
        })
        pending.current = {...pending.current, ...patch}
        router.replace(
            {
                pathname: router.pathname,
                query: {
                    ...router.query,
                    ...pending.current,
                },
            },
            undefined,
            {shallow: true},
        )
    }, [])

    const selectChangeLogView = useCallback((viewKey) => {
        const {views, selectedChangeLogViewKey, setPreferences} = latest.current
        // Guards against a caller naming a view which is not registered
        if (!views.some(it => it.key === viewKey)) return
        // Remembers the choice for the next change log the user opens
        if (viewKey !== selectedChangeLogViewKey) {
            setPreferences({selectedChangeLogViewKey: viewKey})
        }
        // Makes the choice linkable
        replaceQuery({[changeLogViewParam]: viewKey})
    }, [replaceQuery])

    const setSemanticOption = useCallback((name, value) => {
        const {preferences, setPreferences} = latest.current
        const option = semanticChangeLogOptions.find(it => it.name === name)
        // Guards against a caller naming an option which does not exist
        if (!option) return
        if (value !== preferences[option.preference]) {
            setPreferences({[option.preference]: value})
        }
        replaceQuery({[option.param]: formatSemanticOptionParam(value)})
    }, [replaceQuery])

    return {
        views,
        selectedViewKey,
        selectChangeLogView,
        options,
        setSemanticOption,
    }
}
