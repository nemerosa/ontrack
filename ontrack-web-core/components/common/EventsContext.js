import {createContext, useContext, useState} from "react";

export const EventsContext = createContext({})

export const useDashboardEventForRefresh = (name) => {
    const dashboardEventsContext = useContext(EventsContext)

    const [refreshCount, setRefreshCount] = useState(0)

    dashboardEventsContext.subscribeToEvent(name, (_) => {
        setRefreshCount(refreshCount + 1)
    })

    return refreshCount
}


export default function EventsContextProvider({children}) {

    const subscriptions = {}

    const fireEvent = (name, values) => {
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
