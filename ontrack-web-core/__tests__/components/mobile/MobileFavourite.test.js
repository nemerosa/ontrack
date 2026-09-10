import "@testing-library/jest-dom"
import {act, fireEvent, render, screen} from "@testing-library/react"

const callGraphQL = jest.fn()
jest.mock("../../../components/services/GraphQL", () => ({
    callGraphQL: (...args) => callGraphQL(...args),
}))

import MobileFavourite from "@components/mobile/favourites/MobileFavourite"

/** Clicks, and lets whatever the click started settle. */
const tap = async (testId) => act(async () => {
    fireEvent.click(screen.getByTestId(testId))
})

beforeEach(() => {
    callGraphQL.mockReset()
    callGraphQL.mockResolvedValue({})
})

describe('MobileFavourite', () => {

    it('says whether the entity is a favourite, for the eye and for a screen reader', () => {
        render(<MobileFavourite type="project" id={1} name="petclinic" favourite={true}/>)
        const toggle = screen.getByTestId('mobile-favourite-project-1')
        expect(toggle).toHaveAttribute('aria-pressed', 'true')
        expect(toggle).toHaveAccessibleName(/petclinic/)
    })

    it('marks an entity which is not a favourite yet', async () => {
        const onToggled = jest.fn()
        callGraphQL.mockResolvedValue({favouriteProject: {errors: []}})
        render(<MobileFavourite type="project" id={12} name="petclinic" favourite={false} onToggled={onToggled}/>)

        await tap('mobile-favourite-project-12')

        expect(onToggled).toHaveBeenCalledWith(true)
        const [{query, variables}] = callGraphQL.mock.calls[0]
        expect(query).toContain('favouriteProject')
        expect(variables).toEqual({id: 12})
    })

    it('sends the id as a number, whatever GraphQL handed back', async () => {
        // Ids arrive as strings - `id` is a GraphQL `ID` - and the four mutations
        // take an `Int!`, which refuses a string outright.
        callGraphQL.mockResolvedValue({favouriteProject: {errors: []}})
        render(<MobileFavourite type="project" id="286" name="petclinic" favourite={false}/>)

        await tap('mobile-favourite-project-286')

        expect(callGraphQL.mock.calls[0][0].variables).toEqual({id: 286})
    })

    it('unmarks one which is', async () => {
        const onToggled = jest.fn()
        callGraphQL.mockResolvedValue({unfavouriteBranch: {errors: []}})
        render(<MobileFavourite type="branch" id={7} name="main" favourite={true} onToggled={onToggled}/>)

        await tap('mobile-favourite-branch-7')

        expect(onToggled).toHaveBeenCalledWith(false)
        expect(callGraphQL.mock.calls[0][0].query).toContain('unfavouriteBranch')
    })

    it('does not tell the parent it worked when the server refused', async () => {
        // Reporting a state the server did not reach would leave the list showing
        // a favourite that is not one, until the next reload contradicts it.
        const onToggled = jest.fn()
        callGraphQL.mockResolvedValue({favouriteProject: {errors: [{message: "Not authorised"}]}})
        render(<MobileFavourite type="project" id={3} name="petclinic" favourite={false} onToggled={onToggled}/>)

        await tap('mobile-favourite-project-3')

        expect(onToggled).not.toHaveBeenCalled()
        expect(screen.getByTestId('mobile-favourite-project-3')).toBeEnabled()
    })

    it('does not fire a second call while the first is in flight', async () => {
        let resolve
        callGraphQL.mockReturnValue(new Promise(r => {
            resolve = r
        }))
        render(<MobileFavourite type="project" id={4} name="petclinic" favourite={false}/>)

        await tap('mobile-favourite-project-4')
        expect(screen.getByTestId('mobile-favourite-project-4')).toBeDisabled()
        await tap('mobile-favourite-project-4')

        expect(callGraphQL).toHaveBeenCalledTimes(1)
        await act(async () => resolve({favouriteProject: {errors: []}}))
    })
})
