import "@testing-library/jest-dom"
import {act, fireEvent, render, screen} from "@testing-library/react"

let queryResult = {data: null, loading: false, error: null, finished: true}
/** What the screen handed `useQuery` on its last render. */
let queryOptions
let queryDocument

jest.mock("../../../components/services/GraphQL", () => ({
    useQuery: (query, options) => {
        queryDocument = query
        queryOptions = options
        return queryResult
    },
    callGraphQL: jest.fn(),
}))

import MobileProjectScreen, {MOBILE_BRANCH_LIMIT} from "@/app/mobile/project/[id]/ProjectScreen"

const setResult = (result) => {
    queryResult = {data: null, loading: false, error: null, finished: true, ...result}
}

const branch = (id, name, {displayName, favourite = false, disabled = false} = {}) =>
    ({id, name, displayName: displayName ?? name, favourite, disabled})

const project = (branches = [], {favourite = false} = {}) => setResult({
    data: {project: {id: 1, name: 'petclinic', favourite, branches}},
})

/** Types into the branch filter, then lets the debounce fire. */
const filterBy = async (text) => {
    fireEvent.change(screen.getByTestId('mobile-branches-filter'), {target: {value: text}})
    await act(async () => {
        jest.advanceTimersByTime(1000)
    })
}

/** As many branches as the screen is willing to show, plus one. */
const oneTooMany = () => Array.from(
    {length: MOBILE_BRANCH_LIMIT + 1},
    (_, index) => branch(index + 1, `branch-${index + 1}`)
)

beforeEach(() => {
    queryOptions = undefined
    jest.useFakeTimers()
})

afterEach(() => {
    jest.useRealTimers()
})

describe('the mobile project screen', () => {

    it('is the project, named', () => {
        project([branch(10, 'main')])
        render(<MobileProjectScreen id="1"/>)
        expect(screen.getByTestId('mobile-screen-title')).toHaveTextContent('petclinic')
    })

    it('lists the project branches', () => {
        project([branch(10, 'main'), branch(11, 'release/1.0')])
        render(<MobileProjectScreen id="1"/>)
        expect(screen.getByTestId('mobile-branch-10')).toHaveTextContent('main')
        expect(screen.getByTestId('mobile-branch-11')).toHaveTextContent('release/1.0')
    })

    it('puts the branches with the most recent build activity first', () => {
        // A phone shows a handful of rows, and the branch someone reaches for
        // their phone about is the one something just happened on.
        project([branch(10, 'main')])
        render(<MobileProjectScreen id="1"/>)
        expect(queryDocument).toMatch(/order:\s*true/)
    })

    it('calls a branch by its display name when it has one', () => {
        project([branch(10, 'feature-1234', {displayName: 'PRJ-1234'})])
        render(<MobileProjectScreen id="1"/>)
        expect(screen.getByTestId('mobile-branch-10')).toHaveTextContent('PRJ-1234')
    })

    it('sends each branch row to that branch on the phone, not to the desktop page', () => {
        project([branch(10, 'main')])
        render(<MobileProjectScreen id="1"/>)
        expect(screen.getByTestId('mobile-branch-10').querySelector('a'))
            .toHaveAttribute('href', '/mobile/branch/10')
    })

    it('says which branches are disabled', () => {
        project([branch(10, 'retired', {disabled: true})])
        render(<MobileProjectScreen id="1"/>)
        expect(screen.getByTestId('mobile-branch-10')).toHaveTextContent('Disabled')
    })

    it('offers the favourite toggle on each branch, showing the current state', () => {
        project([branch(10, 'main', {favourite: true}), branch(11, 'other')])
        render(<MobileProjectScreen id="1"/>)
        expect(screen.getByTestId('mobile-favourite-branch-10')).toHaveAttribute('aria-pressed', 'true')
        expect(screen.getByTestId('mobile-favourite-branch-11')).toHaveAttribute('aria-pressed', 'false')
    })

    it('offers the favourite toggle on the project itself', () => {
        // This is the screen a user reaches by following a link to a project, so
        // it is where they decide it is worth keeping - the project list is not
        // the only place a favourite can be made.
        project([branch(10, 'main')], {favourite: true})
        render(<MobileProjectScreen id="1"/>)
        expect(screen.getByTestId('mobile-favourite-project-1')).toHaveAttribute('aria-pressed', 'true')
    })

    describe('limiting the list', () => {

        it('asks for one more branch than it shows', () => {
            // A project can hold hundreds of branches, and the `branches` field
            // answers with a plain list - no total. Asking for one extra is what
            // tells the screen whether there is anything beyond what it shows,
            // and it costs one row.
            project([branch(10, 'main')])
            render(<MobileProjectScreen id="1"/>)
            expect(queryOptions.variables.count).toEqual(MOBILE_BRANCH_LIMIT + 1)
        })

        it('shows no more than its limit, whatever came back', () => {
            project(oneTooMany())
            render(<MobileProjectScreen id="1"/>)
            expect(screen.queryAllByTestId(/^mobile-branch-\d+$/)).toHaveLength(MOBILE_BRANCH_LIMIT)
        })

        it('says there are more, and how to reach them', () => {
            project(oneTooMany())
            render(<MobileProjectScreen id="1"/>)
            expect(screen.getByTestId('mobile-branches-truncated')).toHaveTextContent(/filter/i)
        })

        it('says nothing about more branches when the list is whole', () => {
            project([branch(10, 'main')])
            render(<MobileProjectScreen id="1"/>)
            expect(screen.queryByTestId('mobile-branches-truncated')).not.toBeInTheDocument()
        })
    })

    describe('filtering by name', () => {

        it('asks for the whole list until something is typed', () => {
            project([branch(10, 'main')])
            render(<MobileProjectScreen id="1"/>)
            expect(queryOptions.variables.name).toBeNull()
        })

        it('filters on the server, not in the browser', () => {
            // The browser only holds the first page of an ordered list, so a
            // client-side filter could never reach a branch beyond it - which is
            // the only case the filter exists for.
            project([branch(10, 'main')])
            render(<MobileProjectScreen id="1"/>)
            return filterBy('rel').then(() => {
                expect(queryOptions.variables.name).toEqual('(?i)rel')
            })
        })

        it('takes what was typed literally', () => {
            // The server matches the argument as a regular expression.
            project([branch(10, 'main')])
            render(<MobileProjectScreen id="1"/>)
            return filterBy('release/1.0').then(() => {
                expect(queryOptions.variables.name).toEqual('(?i)release/1\\.0')
            })
        })

        it('waits for the typing to settle before asking', () => {
            project([branch(10, 'main')])
            render(<MobileProjectScreen id="1"/>)
            fireEvent.change(screen.getByTestId('mobile-branches-filter'), {target: {value: 'r'}})
            expect(queryOptions.variables.name).toBeNull()
        })

        it('goes back to the whole list when the filter is cleared', async () => {
            project([branch(10, 'main')])
            render(<MobileProjectScreen id="1"/>)
            await filterBy('rel')
            await filterBy('')
            expect(queryOptions.variables.name).toBeNull()
        })

        it('says a filter matched nothing, rather than that the project is empty', async () => {
            project([branch(10, 'main')])
            render(<MobileProjectScreen id="1"/>)
            project([])
            await filterBy('nothing-matches-this')
            expect(screen.getByTestId('mobile-branches-empty')).toHaveTextContent(/no branch matches/i)
        })
    })

    it('says so when the project has no branch at all', () => {
        project([])
        render(<MobileProjectScreen id="1"/>)
        expect(screen.getByTestId('mobile-branches-empty')).toHaveTextContent(/no branch/i)
    })

    it('keeps the list on screen while a filter or a toggle refetches it', () => {
        setResult({
            data: {project: {id: 1, name: 'petclinic', favourite: false, branches: [branch(10, 'main')]}},
            loading: true,
            finished: true,
        })
        render(<MobileProjectScreen id="1"/>)
        expect(screen.getByTestId('mobile-branch-10')).toBeInTheDocument()
    })

    it('does not flash the empty state before the first answer arrives', () => {
        setResult({data: null, loading: true, finished: false})
        render(<MobileProjectScreen id="1"/>)
        expect(screen.queryByTestId('mobile-branches-empty')).not.toBeInTheDocument()
    })

    it('says so when the project could not be loaded', () => {
        setResult({data: null, error: "Boom"})
        render(<MobileProjectScreen id="1"/>)
        expect(screen.getByText(/Boom/)).toBeInTheDocument()
    })

    it('says so when there is no such project, rather than showing an empty one', () => {
        // A link to a project since deleted, or one this user cannot see. An
        // empty branch list would tell them the project exists and has nothing
        // in it, which is a different and wrong answer.
        setResult({data: null, finished: true})
        render(<MobileProjectScreen id="1"/>)
        expect(screen.getByTestId('mobile-project-not-found')).toBeInTheDocument()
    })
})
