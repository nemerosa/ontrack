import "@testing-library/jest-dom"
import {render, screen} from "@testing-library/react"
import EventsContextProvider, {useEventForRefresh} from "@components/common/EventsContext"

function Subscriber() {
    const refreshCount = useEventForRefresh("some.event")
    return <span data-testid="count">{refreshCount}</span>
}

describe('useEventForRefresh', () => {

    it('counts from zero under a provider', () => {
        render(<EventsContextProvider><Subscriber/></EventsContextProvider>)
        expect(screen.getByTestId('count')).toHaveTextContent('0')
    })

    it('renders outside any provider rather than crashing the tree', () => {
        // The context's default value is an empty object, so calling
        // `subscribeToEvent` on it throws. That matters beyond a missing
        // refresh: the mobile UI is its own App Router root with a deliberately
        // shorter provider stack and no `EventsContextProvider`, and shared
        // primitives that only ever want the refresh - `EntityIcon`, and so
        // every promotion medal - would take a whole mobile screen down with
        // them. Without the event there is simply nothing to refresh on.
        render(<Subscriber/>)
        expect(screen.getByTestId('count')).toHaveTextContent('0')
    })
})
