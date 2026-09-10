"use client"

/**
 * What a screen does after one of its favourite stars has been toggled.
 *
 * Every mobile screen carrying `MobileFavourite` refetches rather than patching
 * its copy of the list: the star is controlled, the favourite state lives in the
 * list, and there has to be exactly one answer to "is this a favourite" - the
 * server's. Unstarring on the home screen has to take the row away, and only the
 * server knows it did.
 *
 * The mobile provider stack has no `EventsContextProvider` - the desktop
 * favourite widgets refresh off a `project.favourite` page event - so the
 * counter is local to the screen. That is enough: a phone shows one screen at a
 * time.
 *
 * @returns {{refresh: number, onToggled: function}} `refresh` goes in the
 *   query's `deps`; `onToggled` is handed to every star on the screen.
 */

import {useState} from "react"

export function useFavouriteRefresh() {
    const [refresh, setRefresh] = useState(0)
    return {
        refresh,
        onToggled: () => setRefresh(count => count + 1),
    }
}
