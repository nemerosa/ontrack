import "@testing-library/jest-dom"
import {render, screen} from "@testing-library/react"

let queryResult = {data: null, loading: false, error: null, finished: true}

jest.mock("../../../components/services/GraphQL", () => ({
    useQuery: () => queryResult,
    callGraphQL: jest.fn(),
}))

const switchToDesktopUI = jest.fn()
jest.mock("../../../components/mobile/desktopPreference", () => ({
    switchToDesktopUI: (...args) => switchToDesktopUI(...args),
}))

import MobileBuildScreen from "@/app/mobile/build/[id]/BuildScreen"

const setResult = (result) => {
    queryResult = {data: null, loading: false, error: null, finished: true, ...result}
}

const promotion = (id, levelId, name, {time = '2024-03-01T10:00:00Z', user = 'admin'} = {}) => ({
    id,
    creation: {time, user},
    promotionLevel: {id: levelId, name, image: false},
})

const deployment = (id, environmentName) => ({
    id,
    end: '2024-03-02T10:00:00Z',
    slot: {id: `slot-${id}`, environment: {id: environmentName, name: environmentName}},
})

const validation = (stampId, stampName, statusId, {time = '2024-03-01T11:00:00Z'} = {}) => ({
    validationStamp: {id: stampId, name: stampName, image: false, dataType: null},
    validationRuns: statusId ? [{
        id: stampId * 10,
        lastStatus: {creation: {time, user: 'admin'}, statusID: {id: statusId, name: statusId}},
    }] : [],
})

const build = ({
                   displayName = '1.4.0',
                   description,
                   promotions = [],
                   deployments = [],
                   validations = [],
                   authorizations = [],
               } = {}) => setResult({
    data: {
        build: {
            id: 100,
            name: '20260901055547-36',
            displayName,
            description,
            creation: {time: '2024-03-01T09:00:00Z', user: 'ci'},
            branch: {id: 10, name: 'main', displayName: 'main', project: {id: 1, name: 'petclinic'}},
            authorizations,
            promotionRuns: promotions,
            currentDeployments: deployments,
            validations,
        },
    },
})

/** The shape `isAuthorized` reads. */
const granted = (name, action) => ({name, action, authorized: true})
const refused = (name, action) => ({name, action, authorized: false})

beforeEach(() => {
    switchToDesktopUI.mockClear()
})

describe('the mobile build screen', () => {

    describe('identity', () => {

        it('calls the build by its display name, not its own name', () => {
            // A build name is a timestamp-run pair; the version people talk
            // about is the release property, which `displayName` already is.
            build()
            render(<MobileBuildScreen id="100"/>)
            expect(screen.getByTestId('mobile-screen-title')).toHaveTextContent('1.4.0')
            expect(screen.getByTestId('mobile-screen-title')).not.toHaveTextContent('20260901055547-36')
        })

        it('places the build in its branch and its project', () => {
            build()
            render(<MobileBuildScreen id="100"/>)
            const subtitle = screen.getByTestId('mobile-screen-subtitle')
            expect(subtitle).toHaveTextContent('petclinic')
            expect(subtitle).toHaveTextContent('main')
        })

        it('goes back up to both of them, inside the mobile UI', () => {
            build()
            render(<MobileBuildScreen id="100"/>)
            const hrefs = Array.from(screen.getByTestId('mobile-screen-subtitle').querySelectorAll('a'))
                .map(link => link.getAttribute('href'))
            expect(hrefs).toEqual(['/mobile/project/1', '/mobile/branch/10'])
        })

        it('says when the build was created and by whom', () => {
            build()
            render(<MobileBuildScreen id="100"/>)
            expect(screen.getByTestId('mobile-build-created')).toHaveTextContent('ci')
        })

        it('shows the description when there is one', () => {
            build({description: "Owner search by phone number."})
            render(<MobileBuildScreen id="100"/>)
            expect(screen.getByTestId('mobile-build-description'))
                .toHaveTextContent('Owner search by phone number.')
        })

        it('leaves the description out when there is none', () => {
            build()
            render(<MobileBuildScreen id="100"/>)
            expect(screen.queryByTestId('mobile-build-description')).not.toBeInTheDocument()
        })
    })

    describe('promotions', () => {

        it('lists them, named as well as drawn', () => {
            build({promotions: [promotion(900, 500, 'BRONZE'), promotion(901, 501, 'SILVER')]})
            render(<MobileBuildScreen id="100"/>)
            expect(screen.getByTestId('mobile-build-promotion-900')).toHaveTextContent('BRONZE')
            expect(screen.getByTestId('mobile-build-promotion-901')).toHaveTextContent('SILVER')
        })

        it('says when each promotion happened and who made it', () => {
            // The decision this screen exists for is "should I promote or deploy
            // this?", and who promoted it last, and when, is an input to it.
            build({promotions: [promotion(900, 500, 'BRONZE', {user: 'alice'})]})
            render(<MobileBuildScreen id="100"/>)
            expect(screen.getByTestId('mobile-build-promotion-900')).toHaveTextContent('alice')
        })

        it('says so when the build has none', () => {
            build()
            render(<MobileBuildScreen id="100"/>)
            expect(screen.getByTestId('mobile-build-promotions')).toHaveTextContent(/not been promoted/i)
        })
    })

    describe('deployments', () => {

        it('says where the build is deployed', () => {
            build({deployments: [deployment(800, 'production')]})
            render(<MobileBuildScreen id="100"/>)
            expect(screen.getByTestId('mobile-build-deployment-800')).toHaveTextContent('production')
        })

        it('says so when it is deployed nowhere', () => {
            build()
            render(<MobileBuildScreen id="100"/>)
            expect(screen.getByTestId('mobile-build-deployments')).toHaveTextContent(/not deployed/i)
        })
    })

    describe('validations', () => {

        it('lists the validations with their status', () => {
            // Read-only, and deliberately included even though validations are
            // otherwise out of scope: whether the build is green is the input to
            // the promote-or-deploy decision this screen serves.
            build({validations: [validation(700, 'BUILD', 'PASSED'), validation(701, 'SMOKE', 'FAILED')]})
            render(<MobileBuildScreen id="100"/>)
            expect(screen.getByTestId('mobile-build-validation-700')).toHaveTextContent('BUILD')
            expect(screen.getByTestId('mobile-build-validation-700')).toHaveTextContent('PASSED')
            expect(screen.getByTestId('mobile-build-validation-701')).toHaveTextContent('FAILED')
        })

        it('shows a stamp with no run at all rather than hiding it', () => {
            // "Not run yet" is a different answer from "passed", and on a
            // decision screen the difference matters.
            build({validations: [validation(700, 'BUILD', null)]})
            render(<MobileBuildScreen id="100"/>)
            expect(screen.getByTestId('mobile-build-validation-700')).toBeInTheDocument()
        })

        it('does not drill into a run', () => {
            // Run detail pages are explicitly out of scope.
            build({validations: [validation(700, 'BUILD', 'PASSED')]})
            render(<MobileBuildScreen id="100"/>)
            expect(screen.getByTestId('mobile-build-validation-700').querySelector('a')).toBeNull()
        })

        it('says so when the build has no validation', () => {
            build()
            render(<MobileBuildScreen id="100"/>)
            expect(screen.getByTestId('mobile-build-validations')).toHaveTextContent(/no validation/i)
        })
    })

    describe('the action entry points', () => {

        it('offers to promote a build the user may promote', () => {
            build({authorizations: [granted('build', 'promote')]})
            render(<MobileBuildScreen id="100"/>)
            expect(screen.getByTestId('mobile-build-promote')).toBeInTheDocument()
        })

        it('hides the promote entry point from a user who may not', () => {
            build({authorizations: [refused('build', 'promote')]})
            render(<MobileBuildScreen id="100"/>)
            expect(screen.queryByTestId('mobile-build-promote')).not.toBeInTheDocument()
        })

        it('offers to deploy a build the user may deploy', () => {
            build({authorizations: [granted('slotPipeline', 'create')]})
            render(<MobileBuildScreen id="100"/>)
            expect(screen.getByTestId('mobile-build-deploy')).toBeInTheDocument()
        })

        it('hides the deploy entry point from a user who may not', () => {
            // Also how an instance without the environments licence looks: the
            // server answers `slotPipeline/create` false when the feature is off.
            build({authorizations: [refused('slotPipeline', 'create')]})
            render(<MobileBuildScreen id="100"/>)
            expect(screen.queryByTestId('mobile-build-deploy')).not.toBeInTheDocument()
        })

        it('shows nothing at all to a user who may do neither', () => {
            build({authorizations: []})
            render(<MobileBuildScreen id="100"/>)
            expect(screen.queryByTestId('mobile-build-actions')).not.toBeInTheDocument()
        })

        it('takes the user to the desktop build page until the actions land here', () => {
            // #1724 and #1725 replace this with the dialogs. Until then the
            // entry point has to actually get the user to somewhere they can
            // promote, and switching UI is what the initiative already does for
            // anything it does not cover - cookie first, then navigate.
            build({authorizations: [granted('build', 'promote')]})
            render(<MobileBuildScreen id="100"/>)
            screen.getByTestId('mobile-build-promote').click()
            expect(switchToDesktopUI).toHaveBeenCalledWith('/build/100')
        })

        it('says that is what the entry points do', () => {
            build({authorizations: [granted('build', 'promote')]})
            render(<MobileBuildScreen id="100"/>)
            expect(screen.getByTestId('mobile-build-actions')).toHaveTextContent(/desktop/i)
        })
    })

    it('does not flash an empty build before the first answer arrives', () => {
        setResult({data: null, loading: true, finished: false})
        render(<MobileBuildScreen id="100"/>)
        expect(screen.queryByTestId('mobile-build-promotions')).not.toBeInTheDocument()
    })

    it('says so when the build could not be loaded', () => {
        // Also how "no such build" arrives: `build(id:)` is a non-null field, so
        // an id nobody can see is a GraphQL error rather than a null.
        setResult({data: null, error: "Build ID not found: 100"})
        render(<MobileBuildScreen id="100"/>)
        expect(screen.getByText(/not found/)).toBeInTheDocument()
    })
})
