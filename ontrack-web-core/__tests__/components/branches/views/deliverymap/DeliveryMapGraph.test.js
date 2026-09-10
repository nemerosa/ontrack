import React from "react";
import {act, render, screen, fireEvent} from "@testing-library/react";

// Ant Design uses window.matchMedia for responsive features; jsdom doesn't provide it
Object.defineProperty(window, 'matchMedia', {
    writable: true,
    value: jest.fn().mockImplementation(query => ({
        matches: false,
        media: query,
        onchange: null,
        addListener: jest.fn(),
        removeListener: jest.fn(),
        addEventListener: jest.fn(),
        removeEventListener: jest.fn(),
        dispatchEvent: jest.fn(),
    })),
})
import '@testing-library/jest-dom';

// React Flow does not render in jsdom, and what is under test is WHEN the graph is laid out rather
// than how it draws. The stand-in renders each node's id, position and build, which is exactly what
// a refresh must and must not change.
jest.mock("reactflow", () => ({
    __esModule: true,
    ReactFlow: ({nodes, children}) => (
        <div data-testid="flow">
            {
                nodes.map(node => (
                    <div
                        key={node.id}
                        data-testid={`node-${node.id}`}
                        data-position={`${node.position.x},${node.position.y}`}
                    >
                        {node.data?.checkpoint?.arrival?.build?.name ?? "none"}
                    </div>
                ))
            }
            {children}
        </div>
    ),
    Background: () => null,
    Controls: ({children}) => <div>{children}</div>,
    ControlButton: ({children, ...rest}) => <button {...rest}>{children}</button>,
    applyNodeChanges: (changes, nodes) => nodes,
    MarkerType: {ArrowClosed: 'arrowclosed'},
}))

// The layout is asynchronous and elk is not what is under test; this one answers immediately and
// puts each node somewhere recognisable, so that a position surviving a refresh can be asserted.
jest.mock("../../../../../components/links/GraphUtils", () => ({
    autoLayout: jest.fn(({nodes, edges, setNodes, setEdges}) => {
        setNodes(nodes.map((node, index) => ({...node, position: {x: (index + 1) * 100, y: 50}})))
        setEdges(edges)
        return Promise.resolve()
    }),
}))

import {autoLayout} from "@components/links/GraphUtils";
import DeliveryMapGraph from "@components/branches/views/deliverymap/DeliveryMapGraph";

describe('delivery map layout across refreshes', () => {

    const checkpoint = (id, buildName) => ({
        id,
        type: 'promotion-level',
        name: id,
        data: {promotionLevelId: 12},
        arrival: buildName ? {build: {id: 1, name: buildName}, time: "2026-09-01T10:00:00", lag: 0} : undefined,
    })

    const map = (checkpoints, edges = []) => ({checkpoints, edges})

    const bronze = 'promotion-level:12'
    const silver = 'promotion-level:13'

    beforeEach(() => {
        autoLayout.mockClear()
    })

    it('lays the map out when it first arrives', () => {
        render(<DeliveryMapGraph map={map([checkpoint(bronze, "1")])}/>)
        expect(autoLayout).toHaveBeenCalledTimes(1)
    })

    it('does not lay it out again when a refresh changed nothing', () => {
        // The whole of #1707: a refetch every sixty seconds brings back new objects, and re-running
        // elk on them would reshuffle the map under the reader's cursor
        const {rerender} = render(<DeliveryMapGraph map={map([checkpoint(bronze, "1")])}/>)
        rerender(<DeliveryMapGraph map={map([checkpoint(bronze, "1")])}/>)
        expect(autoLayout).toHaveBeenCalledTimes(1)
    })

    it('does not lay it out again when only the build moved', () => {
        const {rerender} = render(<DeliveryMapGraph map={map([checkpoint(bronze, "1")])}/>)
        rerender(<DeliveryMapGraph map={map([checkpoint(bronze, "2")])}/>)
        expect(autoLayout).toHaveBeenCalledTimes(1)
    })

    it('shows the new build all the same, where the node already was', () => {
        const {rerender} = render(<DeliveryMapGraph map={map([checkpoint(bronze, "1")])}/>)
        expect(screen.getByTestId(`node-${bronze}`)).toHaveAttribute('data-position', '100,50')
        rerender(<DeliveryMapGraph map={map([checkpoint(bronze, "2")])}/>)
        expect(screen.getByTestId(`node-${bronze}`)).toHaveTextContent("2")
        expect(screen.getByTestId(`node-${bronze}`)).toHaveAttribute('data-position', '100,50')
    })

    it('lays the map out again when a checkpoint appears', () => {
        const {rerender} = render(<DeliveryMapGraph map={map([checkpoint(bronze, "1")])}/>)
        rerender(<DeliveryMapGraph map={map([checkpoint(bronze, "1"), checkpoint(silver, "1")])}/>)
        expect(autoLayout).toHaveBeenCalledTimes(2)
    })

    it('lays the map out again when an edge appears', () => {
        const both = [checkpoint(bronze, "1"), checkpoint(silver, "1")]
        const {rerender} = render(<DeliveryMapGraph map={map(both)}/>)
        rerender(
            <DeliveryMapGraph
                map={map(both, [{id: 'unlocks', kind: 'UNLOCKS', source: bronze, target: silver}])}
            />
        )
        expect(autoLayout).toHaveBeenCalledTimes(2)
    })

    it('does not lose a refresh which landed while the layout was still working', () => {
        // elk answers asynchronously. The nodes it hands back carry the checkpoints it was GIVEN,
        // so a refresh arriving in the meantime would be painted over and the map would sit a tick
        // behind until the next one - a minute at the default interval.
        let answer
        autoLayout.mockImplementationOnce(({nodes, edges, setNodes, setEdges}) => new Promise(resolve => {
            answer = () => {
                setNodes(nodes.map((node, index) => ({...node, position: {x: (index + 1) * 100, y: 50}})))
                setEdges(edges)
                resolve()
            }
        }))

        const {rerender} = render(<DeliveryMapGraph map={map([checkpoint(bronze, "1")])}/>)
        rerender(<DeliveryMapGraph map={map([checkpoint(bronze, "2")])}/>)
        act(() => answer())

        expect(screen.getByTestId(`node-${bronze}`)).toHaveTextContent("2")
    })

    it('lays the map out again on demand, however unchanged it is', () => {
        // The manual control the other graphs carry: a map the reader has pulled apart is put back
        // in order from there, and nothing else re-runs the layout for them
        render(<DeliveryMapGraph map={map([checkpoint(bronze, "1")])}/>)
        fireEvent.click(screen.getByTestId('delivery-map-relayout'))
        expect(autoLayout).toHaveBeenCalledTimes(2)
    })

})
