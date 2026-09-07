import "@testing-library/jest-dom"
import {useRef} from "react"
import {fireEvent, render, screen} from "@testing-library/react"
import {PreferencesContext} from "@components/providers/PreferencesProvider"
import useChangeLogViewSelection from "@components/extension/scm/views/useChangeLogViewSelection"

const mockRouter = {
    pathname: "/scm/changelog",
    query: {},
    replace: jest.fn(),
}

jest.mock("next/router", () => ({
    // Next hands out a fresh router object on every render, carrying a *copy* of the query. A
    // callback captured in an earlier render therefore sees the query as it was then, not as it
    // is now — which is exactly the staleness the hook has to defend against.
    useRouter: () => ({
        pathname: mockRouter.pathname,
        query: {...mockRouter.query},
        replace: mockRouter.replace,
    }),
}))

const views = [
    {key: 'classic', name: "Classic"},
    {key: 'semantic', name: "Semantic"},
]

function Probe() {
    const {selectedViewKey, selectChangeLogView, options, setSemanticOption} =
        useChangeLogViewSelection({views})
    // Stands in for the page, which captures these callbacks in the command bar and in a cell
    // built once; neither is rebuilt on every render, so they keep the callback they captured.
    const captured = useRef({selectChangeLogView, setSemanticOption})
    return (
        <>
            <span data-testid="selected">{selectedViewKey}</span>
            <span data-testid="options">{JSON.stringify(options)}</span>
            <button onClick={() => captured.current.selectChangeLogView('semantic')}>Pick semantic</button>
            <button onClick={() => captured.current.selectChangeLogView('classic')}>Pick classic</button>
            <button onClick={() => captured.current.selectChangeLogView('no-such-view')}>Pick unknown</button>
            <button onClick={() => captured.current.setSemanticOption('format', 'jira')}>Pick jira</button>
            <button onClick={() => captured.current.setSemanticOption('emojis', false)}>Emojis off</button>
            <button onClick={() => captured.current.setSemanticOption('commits', true)}>Commits on</button>
            <button onClick={() => captured.current.setSemanticOption('nope', true)}>Set unknown</button>
        </>
    )
}

const renderProbe = ({query = {}, preferences = {}} = {}) => {
    mockRouter.query = {from: "101", to: "102", ...query}
    const setPreferences = jest.fn()
    // A fresh element every time: React bails out of re-rendering an identical element reference
    const tree = () => (
        <PreferencesContext.Provider value={{...preferences, setPreferences, loaded: true}}>
            <Probe/>
        </PreferencesContext.Provider>
    )
    const {rerender} = render(tree())
    rerenderProbe = () => rerender(tree())
    return {setPreferences, rerender: rerenderProbe}
}

let rerenderProbe = () => {
}

const selected = () => screen.getByTestId('selected').textContent
const options = () => JSON.parse(screen.getByTestId('options').textContent)

describe('useChangeLogViewSelection', () => {

    beforeEach(() => {
        mockRouter.replace.mockReset()
    })

    describe('resolving the selected view', () => {

        it('defaults to the classic view when nothing is stored and no parameter is given', () => {
            renderProbe()
            expect(selected()).toBe('classic')
        })

        it('uses the stored preference when no parameter is given', () => {
            renderProbe({preferences: {selectedChangeLogViewKey: 'semantic'}})
            expect(selected()).toBe('semantic')
        })

        it('lets the ?view= parameter override the stored preference', () => {
            renderProbe({query: {view: 'semantic'}, preferences: {selectedChangeLogViewKey: 'classic'}})
            expect(selected()).toBe('semantic')
        })

        it('falls back to the classic view when the ?view= parameter names no known view', () => {
            renderProbe({query: {view: 'no-such-view'}})
            expect(selected()).toBe('classic')
        })

        it('falls back to the classic view when the stored preference names no known view', () => {
            renderProbe({preferences: {selectedChangeLogViewKey: 'no-such-view'}})
            expect(selected()).toBe('classic')
        })

    })

    describe('resolving the semantic options', () => {

        it('defaults to markdown, with emojis and issues, without commits', () => {
            renderProbe()
            expect(options()).toEqual({format: 'markdown', emojis: true, issues: true, commits: false})
        })

        it('uses the stored preferences when no parameter is given', () => {
            renderProbe({
                preferences: {
                    changeLogSemanticFormat: 'jira',
                    changeLogSemanticEmojis: false,
                    changeLogSemanticIssues: false,
                    changeLogSemanticCommits: true,
                }
            })
            expect(options()).toEqual({format: 'jira', emojis: false, issues: false, commits: true})
        })

        it('lets the parameters override the stored preferences', () => {
            renderProbe({
                query: {format: 'slack', emojis: 'false', issues: 'true', commits: 'true'},
                preferences: {
                    changeLogSemanticFormat: 'jira',
                    changeLogSemanticEmojis: true,
                    changeLogSemanticIssues: false,
                    changeLogSemanticCommits: false,
                },
            })
            expect(options()).toEqual({format: 'slack', emojis: false, issues: true, commits: true})
        })

        it('reads a false parameter as false, not as absent', () => {
            // `?emojis=false` on top of a preference of `true` is the whole point of the
            // parameter: a shared link has to reproduce the sender's reading, off included
            renderProbe({query: {emojis: 'false'}, preferences: {changeLogSemanticEmojis: true}})
            expect(options().emojis).toBe(false)
        })

        it('ignores a boolean parameter which is neither true nor false', () => {
            renderProbe({query: {emojis: 'yes'}, preferences: {changeLogSemanticEmojis: false}})
            expect(options().emojis).toBe(false)
        })

        it('ignores an empty format parameter', () => {
            renderProbe({query: {format: ''}, preferences: {changeLogSemanticFormat: 'jira'}})
            expect(options().format).toBe('jira')
        })

    })

    describe('selecting a view', () => {

        it('writes the choice back to the URL, shallowly, so it stays linkable', () => {
            renderProbe()
            fireEvent.click(screen.getByText("Pick semantic"))
            expect(mockRouter.replace).toHaveBeenCalledWith(
                {
                    pathname: "/scm/changelog",
                    query: {from: "101", to: "102", view: 'semantic'},
                },
                undefined,
                {shallow: true},
            )
        })

        it('stores the choice as a preference', () => {
            const {setPreferences} = renderProbe()
            fireEvent.click(screen.getByText("Pick semantic"))
            expect(setPreferences).toHaveBeenCalledWith({selectedChangeLogViewKey: 'semantic'})
        })

        it('does not store the choice again when it is already the stored one', () => {
            const {setPreferences} = renderProbe({preferences: {selectedChangeLogViewKey: 'semantic'}})
            fireEvent.click(screen.getByText("Pick semantic"))
            expect(setPreferences).not.toHaveBeenCalled()
            // ... but the URL is still made to reflect the selection
            expect(mockRouter.replace).toHaveBeenCalled()
        })

        it('ignores a key which names no known view', () => {
            const {setPreferences} = renderProbe()
            fireEvent.click(screen.getByText("Pick unknown"))
            expect(setPreferences).not.toHaveBeenCalled()
            expect(mockRouter.replace).not.toHaveBeenCalled()
        })

    })

    describe('changing an option', () => {

        it('writes the option to the URL and to the preferences', () => {
            const {setPreferences} = renderProbe()
            fireEvent.click(screen.getByText("Pick jira"))
            expect(setPreferences).toHaveBeenCalledWith({changeLogSemanticFormat: 'jira'})
            expect(mockRouter.replace.mock.calls[0][0].query).toEqual({
                from: "101", to: "102", format: 'jira',
            })
        })

        it('writes a false option as false rather than dropping it', () => {
            renderProbe()
            fireEvent.click(screen.getByText("Emojis off"))
            expect(mockRouter.replace.mock.calls[0][0].query.emojis).toBe('false')
        })

        it('keeps the parameters already in the URL, including those of other options', () => {
            renderProbe({query: {view: 'semantic', format: 'jira'}})
            fireEvent.click(screen.getByText("Commits on"))
            expect(mockRouter.replace.mock.calls[0][0].query).toEqual({
                from: "101", to: "102", view: 'semantic', format: 'jira', commits: 'true',
            })
        })

        it('writes back the query as it is at the moment of the click, not at the last render', () => {
            // The cell holding these controls captures the callback; a callback reading a
            // render-time copy of the query would drop whatever was added since
            const {rerender} = renderProbe()
            mockRouter.query = {from: "101", to: "102", view: 'semantic'}
            rerender()
            fireEvent.click(screen.getByText("Pick jira"))
            expect(mockRouter.replace.mock.calls[0][0].query).toEqual({
                from: "101", to: "102", view: 'semantic', format: 'jira',
            })
        })

        it('keeps a change made while the router has not caught up with the previous one', () => {
            // Two switches flipped in a row, which the four in the panel header invite:
            // `router.replace` is asynchronous, so both clicks see the pre-patch query. Building
            // each patch on that query alone would drop the first change from the URL - and its
            // switch would snap back on the next render.
            renderProbe()
            fireEvent.click(screen.getByText("Emojis off"))
            fireEvent.click(screen.getByText("Commits on"))
            expect(mockRouter.replace.mock.calls[1][0].query).toEqual({
                from: "101", to: "102", emojis: 'false', commits: 'true',
            })
        })

        it('stops carrying a change once the router shows it', () => {
            renderProbe()
            fireEvent.click(screen.getByText("Emojis off"))
            // The router has caught up, and the user goes back to the classic reading
            mockRouter.query = {from: "101", to: "102", emojis: 'false'}
            rerenderProbe()
            fireEvent.click(screen.getByText("Pick jira"))
            expect(mockRouter.replace.mock.calls[1][0].query).toEqual({
                from: "101", to: "102", emojis: 'false', format: 'jira',
            })
        })

        it('does not store an option again when it is already the stored one', () => {
            const {setPreferences} = renderProbe({preferences: {changeLogSemanticFormat: 'jira'}})
            fireEvent.click(screen.getByText("Pick jira"))
            expect(setPreferences).not.toHaveBeenCalled()
            expect(mockRouter.replace).toHaveBeenCalled()
        })

        it('ignores a name which is not an option', () => {
            const {setPreferences} = renderProbe()
            fireEvent.click(screen.getByText("Set unknown"))
            expect(setPreferences).not.toHaveBeenCalled()
            expect(mockRouter.replace).not.toHaveBeenCalled()
        })

    })

})
