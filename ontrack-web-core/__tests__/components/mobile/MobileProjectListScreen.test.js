import "@testing-library/jest-dom"
import {act, fireEvent, render, screen} from "@testing-library/react"

let queryResult = {data: null, loading: false, error: null, finished: true}
/** The options the screen handed `useQuery` on its last render. */
let queryOptions

jest.mock("../../../components/services/GraphQL", () => ({
    useQuery: (query, options) => {
        queryOptions = options
        return queryResult
    },
    callGraphQL: jest.fn(),
}))

import MobileProjectListScreen from "@/app/mobile/projects/ProjectListScreen"

const setResult = (result) => {
    queryResult = {data: null, loading: false, error: null, finished: true, ...result}
}

const projects = (...list) => setResult({data: {projects: list}})

const project = (id, name, favourite = false, disabled = false) => ({id, name, favourite, disabled})

/** Types into the filter, then lets the debounce fire. */
const filterBy = async (text) => {
    fireEvent.change(screen.getByTestId('mobile-projects-filter'), {target: {value: text}})
    await act(async () => {
        jest.advanceTimersByTime(1000)
    })
}

beforeEach(() => {
    queryOptions = undefined
    jest.useFakeTimers()
})

afterEach(() => {
    jest.useRealTimers()
})

describe('the mobile project list', () => {

    it('lists every project, favourite or not', () => {
        // This is the screen the home screen's empty state sends a first-time
        // user to, so it cannot itself be filtered down to favourites.
        projects(project(1, 'petclinic', true), project(2, 'common-library'))
        render(<MobileProjectListScreen/>)
        expect(screen.getByTestId('mobile-project-1')).toHaveTextContent('petclinic')
        expect(screen.getByTestId('mobile-project-2')).toHaveTextContent('common-library')
    })

    it('offers the favourite toggle on each of them, showing the current state', () => {
        projects(project(1, 'petclinic', true), project(2, 'common-library'))
        render(<MobileProjectListScreen/>)
        expect(screen.getByTestId('mobile-favourite-project-1')).toHaveAttribute('aria-pressed', 'true')
        expect(screen.getByTestId('mobile-favourite-project-2')).toHaveAttribute('aria-pressed', 'false')
    })

    it('says which projects are disabled', () => {
        projects(project(3, 'retired-thing', false, true))
        render(<MobileProjectListScreen/>)
        expect(screen.getByTestId('mobile-project-3')).toHaveTextContent('Disabled')
    })

    describe('filtering by name', () => {

        it('asks the server for the whole list until something is typed', () => {
            // An instance can hold hundreds of projects, which is more than a
            // phone can be scrolled through - but the filter has to start empty
            // or the screen would open on nothing.
            projects(project(1, 'petclinic'))
            render(<MobileProjectListScreen/>)
            expect(queryOptions.variables.pattern).toBeNull()
        })

        it('filters on the server, not in the browser', () => {
            // The list the browser holds is the answer to the last query; a
            // client-side filter could only narrow that, never reach a project
            // the server never sent.
            projects(project(1, 'petclinic'))
            render(<MobileProjectListScreen/>)
            return filterBy('pet').then(() => {
                expect(queryOptions.variables.pattern).toEqual('pet')
            })
        })

        it('waits for the typing to settle before asking', () => {
            projects(project(1, 'petclinic'))
            render(<MobileProjectListScreen/>)
            fireEvent.change(screen.getByTestId('mobile-projects-filter'), {target: {value: 'p'}})
            // One request per keystroke on a list this size is what the debounce
            // is there to prevent.
            expect(queryOptions.variables.pattern).toBeNull()
        })

        it('treats a whitespace-only filter as no filter', async () => {
            // The server tests the pattern with `isNullOrBlank` and answers a
            // blank one with the whole list, so a screen calling itself filtered
            // would head every project on the instance with "Matching projects".
            projects(project(1, 'petclinic'))
            render(<MobileProjectListScreen/>)
            await filterBy('   ')
            expect(queryOptions.variables.pattern).toBeNull()
            expect(screen.getByTestId('mobile-projects')).toHaveTextContent('All projects')
        })

        it('sends the pattern without the spaces around it', async () => {
            projects(project(1, 'petclinic'))
            render(<MobileProjectListScreen/>)
            await filterBy('  pet  ')
            expect(queryOptions.variables.pattern).toEqual('pet')
        })

        it('goes back to the whole list when the filter is cleared', async () => {
            projects(project(1, 'petclinic'))
            render(<MobileProjectListScreen/>)
            await filterBy('pet')
            await filterBy('')
            expect(queryOptions.variables.pattern).toBeNull()
        })

        it('says a filter matched nothing, rather than showing the empty instance message', async () => {
            projects(project(1, 'petclinic'))
            render(<MobileProjectListScreen/>)
            projects()
            await filterBy('nothing-matches-this')
            expect(screen.getByTestId('mobile-projects-empty')).toHaveTextContent(/no project matches/i)
        })
    })

    it('keeps the list on screen while a filter or a toggle refetches it', () => {
        setResult({data: {projects: [project(1, 'petclinic')]}, loading: true, finished: true})
        render(<MobileProjectListScreen/>)
        expect(screen.getByTestId('mobile-project-1')).toBeInTheDocument()
    })

    it('says so when there is no project at all', () => {
        projects()
        render(<MobileProjectListScreen/>)
        expect(screen.getByTestId('mobile-projects-empty')).toHaveTextContent(/no project on this instance/i)
    })

    it('does not flash the empty state before the first answer arrives', () => {
        setResult({data: null, loading: true, finished: false})
        render(<MobileProjectListScreen/>)
        expect(screen.queryByTestId('mobile-projects-empty')).not.toBeInTheDocument()
    })

    it('says so when the projects could not be loaded', () => {
        setResult({data: null, error: "Boom"})
        render(<MobileProjectListScreen/>)
        expect(screen.getByText(/Boom/)).toBeInTheDocument()
        expect(screen.queryByTestId('mobile-projects-empty')).not.toBeInTheDocument()
    })
})
