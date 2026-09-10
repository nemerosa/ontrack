import "@testing-library/jest-dom"
import {render, screen} from "@testing-library/react"

let queryResult = {data: null, loading: false, error: null, finished: true}

jest.mock("../../../components/services/GraphQL", () => ({
    useQuery: () => queryResult,
    callGraphQL: jest.fn(),
}))

import MobileHomeScreen from "@/app/mobile/HomeScreen"

const setResult = (result) => {
    queryResult = {data: null, loading: false, error: null, finished: true, ...result}
}

const favourites = (projects = [], branches = []) => setResult({data: {projects, branches}})

const project = (id, name) => ({id, name, favourite: true})
const branch = (id, name, projectName) => ({
    id,
    name,
    displayName: name,
    favourite: true,
    project: {id: 100 + id, name: projectName},
})

describe('the mobile home screen', () => {

    it('lists the favourite projects', () => {
        favourites([project(1, 'petclinic'), project(2, 'common-library')])
        render(<MobileHomeScreen/>)
        expect(screen.getByTestId('mobile-project-1')).toHaveTextContent('petclinic')
        expect(screen.getByTestId('mobile-project-2')).toHaveTextContent('common-library')
    })

    it('lists the favourite branches, naming the project each belongs to', () => {
        // Branches come from every project at once, so a bare branch name would
        // be ambiguous the moment two projects both have a `main`.
        favourites([], [branch(5, 'main', 'petclinic')])
        render(<MobileHomeScreen/>)
        const row = screen.getByTestId('mobile-branch-5')
        expect(row).toHaveTextContent('main')
        expect(row).toHaveTextContent('petclinic')
    })

    it('offers to unfavourite from the home screen itself', () => {
        favourites([project(1, 'petclinic')], [branch(5, 'main', 'petclinic')])
        render(<MobileHomeScreen/>)
        expect(screen.getByTestId('mobile-favourite-project-1')).toHaveAttribute('aria-pressed', 'true')
        expect(screen.getByTestId('mobile-favourite-branch-5')).toHaveAttribute('aria-pressed', 'true')
    })

    it('explains what favourites are, rather than showing a blank screen', () => {
        favourites([], [])
        render(<MobileHomeScreen/>)
        expect(screen.getByTestId('mobile-favourites-empty')).toBeInTheDocument()
        // And a way out of the empty state, or a first-time user is stuck on it.
        expect(screen.getByTestId('mobile-favourites-empty-projects')).toHaveAttribute('href', '/mobile/projects')
    })

    it('does not flash the empty state before the first answer arrives', () => {
        setResult({data: null, loading: true, finished: false})
        render(<MobileHomeScreen/>)
        expect(screen.queryByTestId('mobile-favourites-empty')).not.toBeInTheDocument()
    })

    it('keeps the favourites on screen while a toggle refetches them', () => {
        // Every star tap refetches, so a skeleton painted over the list on
        // `loading` alone would flash on every single interaction.
        setResult({data: {projects: [project(1, 'petclinic')], branches: []}, loading: true, finished: true})
        render(<MobileHomeScreen/>)
        expect(screen.getByTestId('mobile-project-1')).toBeInTheDocument()
    })

    it('shows only the sections which have something in them', () => {
        favourites([project(1, 'petclinic')], [])
        render(<MobileHomeScreen/>)
        expect(screen.getByTestId('mobile-favourite-projects')).toBeInTheDocument()
        expect(screen.queryByTestId('mobile-favourite-branches')).not.toBeInTheDocument()
        expect(screen.queryByTestId('mobile-favourites-empty')).not.toBeInTheDocument()
    })

    it('says so when the favourites could not be loaded', () => {
        setResult({data: null, error: "Boom"})
        render(<MobileHomeScreen/>)
        expect(screen.getByText(/Boom/)).toBeInTheDocument()
        expect(screen.queryByTestId('mobile-favourites-empty')).not.toBeInTheDocument()
    })
})
