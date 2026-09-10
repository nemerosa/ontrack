import "@testing-library/jest-dom"
import {fireEvent, render, screen} from "@testing-library/react"

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

import MobileBranchScreen, {MOBILE_BUILD_PAGE_SIZE} from "@/app/mobile/branch/[id]/BranchScreen"

const setResult = (result) => {
    queryResult = {data: null, loading: false, error: null, finished: true, ...result}
}

const build = (id, name, {displayName, time, promotions = [], deployments = []} = {}) => ({
    id,
    name,
    displayName: displayName ?? name,
    creation: {time: time ?? '2024-03-01T10:00:00Z'},
    promotionRuns: promotions.map(([runId, levelId, levelName]) => ({
        id: runId,
        promotionLevel: {id: levelId, name: levelName, image: false},
    })),
    currentDeployments: deployments.map(([pipelineId, environmentName, qualifier = '']) => ({
        id: pipelineId,
        slot: {id: `slot-${pipelineId}`, qualifier, environment: {id: environmentName, name: environmentName}},
    })),
})

const branch = (builds = [], {nextPage = null, disabled = false, favourite = false} = {}) => setResult({
    data: {
        branch: {
            id: 10,
            name: 'main',
            displayName: 'main',
            disabled,
            favourite,
            project: {id: 1, name: 'petclinic'},
            buildsPaginated: {
                pageInfo: {nextPage},
                pageItems: builds,
            },
        },
    },
})

describe('the mobile branch screen', () => {

    beforeEach(() => {
        queryOptions = undefined
    })

    it('is the branch, named, and says which project it belongs to', () => {
        // A bare branch name says too little - two projects can both have a
        // `main`, and this screen is reachable from a favourites list mixing
        // branches from every project.
        branch([build(100, '1')])
        render(<MobileBranchScreen id="10"/>)
        expect(screen.getByTestId('mobile-screen-title')).toHaveTextContent('main')
        expect(screen.getByTestId('mobile-screen-subtitle')).toHaveTextContent('petclinic')
    })

    it('goes back up to the project it belongs to', () => {
        branch([build(100, '1')])
        render(<MobileBranchScreen id="10"/>)
        expect(screen.getByTestId('mobile-screen-subtitle').querySelector('a'))
            .toHaveAttribute('href', '/mobile/project/1')
    })

    it('offers the favourite toggle on the branch itself', () => {
        branch([build(100, '1')], {favourite: true})
        render(<MobileBranchScreen id="10"/>)
        expect(screen.getByTestId('mobile-favourite-branch-10')).toHaveAttribute('aria-pressed', 'true')
    })

    it('says when the branch is disabled', () => {
        branch([build(100, '1')], {disabled: true})
        render(<MobileBranchScreen id="10"/>)
        expect(screen.getByTestId('mobile-branch-disabled')).toBeInTheDocument()
    })

    it('shows the latest builds as cards, in the order the server gave them', () => {
        // The desktop UI renders builds as a matrix with a column per validation
        // stamp, which has no phone form at all. A card per build is what
        // replaces it - and the server already answers most-recent-first, which
        // the screen must not undo.
        branch([build(100, '3'), build(101, '2'), build(102, '1')])
        render(<MobileBranchScreen id="10"/>)
        const cards = screen.queryAllByTestId(/^mobile-build-\d+$/)
        expect(cards.map(card => card.getAttribute('data-testid')))
            .toEqual(['mobile-build-100', 'mobile-build-101', 'mobile-build-102'])
    })

    it('calls a build by its display name', () => {
        // A build name is a timestamp-run pair; the version people talk about is
        // the release property, which is exactly what `displayName` answers with
        // when it is set.
        branch([build(100, '20260901055547-36', {displayName: '1.4.0'})])
        render(<MobileBranchScreen id="10"/>)
        const card = screen.getByTestId('mobile-build-100')
        expect(card).toHaveTextContent('1.4.0')
        expect(card).not.toHaveTextContent('20260901055547-36')
    })

    it('says when each build happened', () => {
        branch([build(100, '1', {time: '2024-03-01T10:00:00Z'})])
        render(<MobileBranchScreen id="10"/>)
        expect(screen.getByTestId('mobile-build-100-time')).not.toBeEmptyDOMElement()
    })

    it('shows the promotions of a build, named rather than only drawn', () => {
        // "Legible without zooming" is the acceptance criterion: a 16px medal on
        // a phone is a coloured dot, so the name goes beside it.
        branch([build(100, '1', {promotions: [[900, 500, 'BRONZE'], [901, 501, 'SILVER']]})])
        render(<MobileBranchScreen id="10"/>)
        expect(screen.getByTestId('mobile-promotion-900')).toHaveTextContent('BRONZE')
        expect(screen.getByTestId('mobile-promotion-901')).toHaveTextContent('SILVER')
    })

    it('shows where a build is deployed', () => {
        branch([build(100, '1', {deployments: [[800, 'staging']]})])
        render(<MobileBranchScreen id="10"/>)
        expect(screen.getByTestId('mobile-deployment-800')).toHaveTextContent('staging')
    })

    it('says which slot a deployment is in when the project has more than one', () => {
        // A project can have several slots in the same environment, told apart
        // only by their qualifier. Dropping it would show two identical rows.
        branch([build(100, '1', {deployments: [[800, 'staging', 'blue']]})])
        render(<MobileBranchScreen id="10"/>)
        expect(screen.getByTestId('mobile-deployment-800')).toHaveTextContent('blue')
    })

    it('shows nothing about promotions or deployments when a build has neither', () => {
        branch([build(100, '1')])
        render(<MobileBranchScreen id="10"/>)
        expect(screen.queryByTestId('mobile-build-100-promotions')).not.toBeInTheDocument()
        expect(screen.queryByTestId('mobile-build-100-deployments')).not.toBeInTheDocument()
    })

    it('does not send a build card anywhere yet', () => {
        // The build screen is its own issue. A tap that 404s is worse than a
        // card that does not move.
        branch([build(100, '1')])
        render(<MobileBranchScreen id="10"/>)
        expect(screen.getByTestId('mobile-build-100').querySelector('a')).toBeNull()
    })

    describe('reaching older builds', () => {

        it('starts with one page of builds', () => {
            branch([build(100, '1')])
            render(<MobileBranchScreen id="10"/>)
            expect(queryOptions.variables.size).toEqual(MOBILE_BUILD_PAGE_SIZE)
        })

        it('asks for one more page when the user wants more', () => {
            branch([build(100, '1')], {nextPage: {offset: MOBILE_BUILD_PAGE_SIZE}})
            render(<MobileBranchScreen id="10"/>)
            fireEvent.click(screen.getByTestId('mobile-builds-more'))
            expect(queryOptions.variables.size).toEqual(MOBILE_BUILD_PAGE_SIZE * 2)
        })

        it('offers nothing more to load once the branch is exhausted', () => {
            branch([build(100, '1')])
            render(<MobileBranchScreen id="10"/>)
            expect(screen.queryByTestId('mobile-builds-more')).not.toBeInTheDocument()
        })
    })

    it('says so when the branch has no build yet', () => {
        branch([])
        render(<MobileBranchScreen id="10"/>)
        expect(screen.getByTestId('mobile-builds-empty')).toHaveTextContent(/no build/i)
    })

    it('keeps the builds on screen while another page is fetched', () => {
        setResult({
            data: {
                branch: {
                    id: 10, name: 'main', displayName: 'main', favourite: false,
                    project: {id: 1, name: 'petclinic'},
                    buildsPaginated: {pageInfo: {nextPage: null}, pageItems: [build(100, '1')]},
                },
            },
            loading: true,
            finished: true,
        })
        render(<MobileBranchScreen id="10"/>)
        expect(screen.getByTestId('mobile-build-100')).toBeInTheDocument()
    })

    it('does not flash the empty state before the first answer arrives', () => {
        setResult({data: null, loading: true, finished: false})
        render(<MobileBranchScreen id="10"/>)
        expect(screen.queryByTestId('mobile-builds-empty')).not.toBeInTheDocument()
    })

    it('says so when the branch could not be loaded', () => {
        // Also how "no such branch" arrives: the root `branch(id:)` field is
        // non-null, so a missing one is a GraphQL error rather than a null.
        setResult({data: null, error: "Boom"})
        render(<MobileBranchScreen id="10"/>)
        expect(screen.getByText(/Boom/)).toBeInTheDocument()
        expect(screen.queryByTestId('mobile-builds-empty')).not.toBeInTheDocument()
    })
})
