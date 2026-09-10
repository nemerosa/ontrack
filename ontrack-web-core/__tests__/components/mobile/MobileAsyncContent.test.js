import "@testing-library/jest-dom"
import {render, screen} from "@testing-library/react"
import MobileAsyncContent from "@components/mobile/layout/MobileAsyncContent"

const renderWith = (state, {isEmpty = false} = {}) => render(
    <MobileAsyncContent
        state={{loading: false, error: null, finished: true, ...state}}
        errorMessage="Could not load the things."
        isEmpty={isEmpty}
        empty={<div data-testid="empty">Nothing here</div>}
    >
        <div data-testid="content">The list</div>
    </MobileAsyncContent>
)

describe('MobileAsyncContent', () => {

    it('shows a skeleton, not an empty state, before the first answer lands', () => {
        // `useQuery` starts with `loading` false and only flips it inside its
        // effect, so a screen trusting `loading` alone reads as loaded here.
        renderWith({loading: false, finished: false}, {isEmpty: true})
        expect(screen.queryByTestId('empty')).not.toBeInTheDocument()
        expect(screen.queryByTestId('content')).not.toBeInTheDocument()
    })

    it('shows the content once it is there', () => {
        renderWith({})
        expect(screen.getByTestId('content')).toBeInTheDocument()
    })

    it('keeps the previous answer on screen while the next one is fetched', () => {
        // Every favourite toggle refetches, so painting a skeleton over the list
        // on each tap is a flash the user gets on every interaction.
        renderWith({loading: true, finished: true})
        expect(screen.getByTestId('content')).toBeInTheDocument()
    })

    it('marks the content as stale while that refetch runs', () => {
        const {container} = renderWith({loading: true, finished: true})
        expect(container.querySelector('.ot-mobile-async')).toHaveAttribute('data-loading', 'true')
    })

    it('does not mark it stale once the answer has landed', () => {
        const {container} = renderWith({})
        expect(container.querySelector('.ot-mobile-async')).not.toHaveAttribute('data-loading')
    })

    it('shows the empty state when a finished query found nothing', () => {
        renderWith({}, {isEmpty: true})
        expect(screen.getByTestId('empty')).toBeInTheDocument()
    })

    it('does not claim there is nothing while it is still asking', () => {
        // A refetch that has not answered yet cannot support "there is nothing".
        renderWith({loading: true, finished: true}, {isEmpty: true})
        expect(screen.queryByTestId('empty')).not.toBeInTheDocument()
    })

    it('reports an error instead of anything else', () => {
        renderWith({error: "Boom"}, {isEmpty: true})
        expect(screen.getByText(/Could not load the things/)).toBeInTheDocument()
        expect(screen.getByText(/Boom/)).toBeInTheDocument()
        expect(screen.queryByTestId('empty')).not.toBeInTheDocument()
        expect(screen.queryByTestId('content')).not.toBeInTheDocument()
    })
})
