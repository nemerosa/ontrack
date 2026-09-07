import "@testing-library/jest-dom"
import {fireEvent, render, screen} from "@testing-library/react"

let queryResult

// The cell renders whatever the `semantic` field returns; the query itself is the server's job
jest.mock("../../../../../components/services/GraphQL", () => ({
    useQuery: () => queryResult,
}))

jest.mock("../../../../../components/extension/scm/views/useTemplatingRenderers", () => () => ([
    {id: 'markdown', name: "Markdown"},
    {id: 'jira', name: "Jira"},
]))

// The grid cell is a frame; what matters here is its content and its header controls
jest.mock("../../../../../components/grid/GridCell", () => ({title, extra, children}) => (
    <div>
        <span data-testid="cell-title">{title}</span>
        <div data-testid="cell-extra">{extra}</div>
        <div data-testid="cell-content">{children}</div>
    </div>
))

const copied = []
jest.mock("copy-to-clipboard", () => (text) => {
    copied.push(text)
    return true
})

import ChangeLogSemantic from "@components/extension/scm/views/ChangeLogSemantic";

const setResult = (result) => {
    queryResult = {data: '', loading: false, error: null, finished: true, ...result}
}

const options = {format: 'markdown', emojis: true, issues: true, commits: false}

const renderCell = ({onOptionChange = jest.fn()} = {}) => {
    const tree = () => (
        <ChangeLogSemantic
            id="semantic"
            from={101}
            to={102}
            options={options}
            onOptionChange={onOptionChange}
        />
    )
    const {rerender} = render(tree())
    return {onOptionChange, rerender: () => rerender(tree())}
}

beforeEach(() => {
    copied.length = 0
    setResult({})
})

describe('ChangeLogSemantic', () => {

    it('shows the rendered change log as it comes back, raw', () => {
        setResult({data: "✨ Features:\n\n* **api** - search owners"})
        renderCell()
        expect(screen.getByTestId('semantic-content'))
            .toHaveTextContent("✨ Features:")
        expect(screen.queryByTestId('semantic-empty')).not.toBeInTheDocument()
    })

    /**
     * `SemanticChangelogRenderingServiceImpl` drops every commit whose subject carries no
     * conventional-commit type, so a project not using them gets an empty string back. The view
     * says so rather than rendering a blank panel.
     */
    it('explains the empty result rather than showing a blank panel', () => {
        setResult({data: ''})
        renderCell()
        expect(screen.getByTestId('semantic-empty'))
            .toHaveTextContent("no conventional-commit types")
        expect(screen.queryByTestId('semantic-content')).not.toBeInTheDocument()
    })

    it('says nothing about the result while it is still being fetched', () => {
        setResult({data: '', finished: false})
        renderCell()
        expect(screen.queryByTestId('semantic-empty')).not.toBeInTheDocument()
    })

    it('reports a rendering failure instead of reading as empty', () => {
        setResult({data: '', error: "Boom"})
        renderCell()
        expect(screen.queryByTestId('semantic-empty')).not.toBeInTheDocument()
        expect(screen.getByText("The semantic change log could not be rendered.")).toBeInTheDocument()
    })

    it('copies the rendered text', () => {
        setResult({data: "✨ Features:"})
        renderCell()
        fireEvent.click(screen.getByTestId('semantic-copy'))
        expect(copied).toEqual(["✨ Features:"])
    })

    /**
     * The rendering changes under this cell every time an option is touched, so a flag saying
     * only *that* something was copied would leave the button disabled over text nobody has
     * copied - the new format, in particular, could never be copied at all.
     */
    it('offers to copy again once the rendering changes', () => {
        setResult({data: "✨ Features:"})
        const {rerender} = renderCell()
        fireEvent.click(screen.getByTestId('semantic-copy'))
        expect(screen.getByTestId('semantic-copy')).toBeDisabled()

        setResult({data: "h4. Features:"})
        rerender()

        expect(screen.getByTestId('semantic-copy')).toBeEnabled()
        fireEvent.click(screen.getByTestId('semantic-copy'))
        expect(copied).toEqual(["✨ Features:", "h4. Features:"])
    })

    it('reports a toggled option to its owner', () => {
        setResult({data: "✨ Features:"})
        const {onOptionChange} = renderCell()
        fireEvent.click(screen.getByTestId('semantic-option-emojis'))
        expect(onOptionChange).toHaveBeenCalledWith('emojis', false)
    })

    it('offers the commits toggle from inside the view, which is where a reader looks for it', () => {
        setResult({data: ''})
        renderCell()
        expect(screen.getByTestId('semantic-option-commits')).toBeInTheDocument()
    })

})
