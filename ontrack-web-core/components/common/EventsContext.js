import {createContext, useContext, useState} from "react";

export const EventsContext = createContext({})

/*
 * `subscribeToEvent?.(...)` and not `subscribeToEvent(...)`: the context's
 * default value is an empty object, so a subscriber rendered outside any
 * provider would throw and take its whole tree down. That is not hypothetical -
 * the mobile UI is its own App Router root with a deliberately shorter provider
 * stack and no `EventsContextProvider`, and shared primitives which only ever
 * want the refresh (`EntityIcon`, and so every promotion medal) are used from
 * it. Outside a provider nothing is ever fired, so there is nothing to refresh
 * on, and a counter stuck at 0 is the right answer rather than a degraded one.
 */

export const useEventForRefresh = (name) => {
    const dashboardEventsContext = useContext(EventsContext)

    const [refreshCount, setRefreshCount] = useState(0)

    dashboardEventsContext.subscribeToEvent?.(name, (_) => {
        setRefreshCount(refreshCount + 1)
    })

    return refreshCount
}

export const useEventsForRefresh = (names) => {
    const dashboardEventsContext = useContext(EventsContext)

    const [refreshCount, setRefreshCount] = useState(0)

    for (const name of names) {
        dashboardEventsContext.subscribeToEvent?.(name, (_) => {
            setRefreshCount(refreshCount + 1)
        })
    }

    return refreshCount
}


export default function EventsContextProvider({children}) {

    const subscriptions = {}

    const fireEvent = (name, values) => {
        console.debug("Event fired: ", {name, values})
        const callbacks = subscriptions[name]
        if (callbacks) {
            callbacks.forEach(callback => {
                callback(values)
            })
        }
    }

    const subscribeToEvent = (name, callback) => {
        let eventSubscriptions = subscriptions[name]
        if (!eventSubscriptions) {
            eventSubscriptions = []
            subscriptions[name] = eventSubscriptions
        }
        eventSubscriptions.push(callback)
    }

    return <EventsContext.Provider value={{fireEvent, subscribeToEvent}}>{children}</EventsContext.Provider>
}
