import React from "react";
import {render, screen} from "@testing-library/react";

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

import DeliveryMapHeader from "@components/branches/views/deliverymap/DeliveryMapHeader";

describe('delivery map header', () => {

    const head = {
        id: 42,
        name: "20260901-7",
        displayName: "20260901-7",
        creation: {time: "2026-09-01T10:00:00"},
    }

    it("names the branch's latest build", () => {
        // The frame of reference every checkpoint's lag is counted against: "build 42" means little
        // until you know the branch is at 47
        render(<DeliveryMapHeader head={head}/>)
        expect(screen.getByTestId('delivery-map-header')).toHaveTextContent("20260901-7")
    })

    it("links to that build", () => {
        render(<DeliveryMapHeader head={head}/>)
        expect(screen.getByText("20260901-7").closest('a')).toHaveAttribute('href', '/build/42')
    })

    it("says so when the branch has no build at all", () => {
        // There is nothing to be behind, and every checkpoint's lag marker is absent for that reason
        render(<DeliveryMapHeader head={null}/>)
        expect(screen.getByTestId('delivery-map-header')).toHaveTextContent("No build on this branch yet")
    })

    it("carries the controls acting on the whole view", () => {
        // The auto refresh button: the header is the view's toolbar, and the controls acting on the
        // drawing itself sit in the graph's own control bar instead
        render(<DeliveryMapHeader head={head} extra={<button>Auto refresh</button>}/>)
        expect(screen.getByRole('button', {name: "Auto refresh"})).toBeInTheDocument()
    })

    it("survives a build with no creation time", () => {
        render(<DeliveryMapHeader head={{id: 42, name: "20260901-7", displayName: "20260901-7"}}/>)
        expect(screen.getByTestId('delivery-map-header')).toHaveTextContent("20260901-7")
    })

})
