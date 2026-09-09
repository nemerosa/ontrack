import React from "react";
import {fireEvent, render, screen} from "@testing-library/react";

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

import {EventsContext} from "@components/common/EventsContext";
import PromotionLevelCheckpoint
    from "@components/branches/views/deliverymap/checkpoints/PromotionLevelCheckpoint";
import ValidationStampCheckpoint
    from "@components/branches/views/deliverymap/checkpoints/ValidationStampCheckpoint";
import ValidationStampPatternCheckpoint
    from "@components/branches/views/deliverymap/checkpoints/ValidationStampPatternCheckpoint";
import UnknownCheckpoint from "@components/branches/views/deliverymap/checkpoints/UnknownCheckpoint";
import SlotCheckpoint from "@components/extension/environments/deliverymap/SlotCheckpoint";

beforeEach(() => {
    // The entity icon fetches its image on mount; it is not what these tests are about
    global.fetch = jest.fn().mockResolvedValue({
        ok: true,
        json: () => Promise.resolve({dataURL: 'data:image/png;base64,AAAA'}),
    })
})

const build = (name) => ({id: 42, name, displayName: name})

// `EntityIcon` subscribes to the page event bus on mount; these tests are about what a checkpoint
// says, not about refreshes
const withEvents = (element) => render(
    <EventsContext.Provider value={{fireEvent: jest.fn(), subscribeToEvent: jest.fn()}}>
        {element}
    </EventsContext.Provider>
)

describe('promotion level checkpoint', () => {

    it('names the latest build promoted there', () => {
        withEvents(<PromotionLevelCheckpoint checkpoint={{
            id: 'promotion-level:12',
            type: 'promotion-level',
            name: "SILVER",
            data: {promotionLevelId: 12, image: false},
            arrival: {build: build("20260901-1"), time: "2026-09-01T10:00:00"},
        }}/>)
        expect(screen.getByText("SILVER")).toBeInTheDocument()
        expect(screen.getByText("20260901-1")).toBeInTheDocument()
    })

    it('says in words when nothing has ever been promoted there', () => {
        // Otherwise an unreached checkpoint and one still loading look alike
        withEvents(<PromotionLevelCheckpoint checkpoint={{
            id: 'promotion-level:12',
            type: 'promotion-level',
            name: "GOLD",
            data: {promotionLevelId: 12, image: false},
        }}/>)
        expect(screen.getByText("Never promoted")).toBeInTheDocument()
    })

})

describe('validation stamp checkpoint', () => {

    it('shows the status of the run the build arrived with', () => {
        // The one kind where arriving and succeeding come apart
        withEvents(<ValidationStampCheckpoint checkpoint={{
            id: 'validation-stamp:3',
            type: 'validation-stamp',
            name: "QUALITY",
            data: {validationStampId: 3, image: false},
            arrival: {
                build: build("20260901-2"),
                time: "2026-09-01T10:00:00",
                status: {id: 'FAILED', name: "Failed", passed: false},
            },
        }}/>)
        expect(screen.getByText("QUALITY")).toBeInTheDocument()
        expect(screen.getByText("Failed")).toBeInTheDocument()
        expect(screen.getByText("20260901-2")).toBeInTheDocument()
    })

    it('says in words when the stamp has never run', () => {
        withEvents(<ValidationStampCheckpoint checkpoint={{
            id: 'validation-stamp:3',
            type: 'validation-stamp',
            name: "QUALITY",
            data: {validationStampId: 3, image: false},
        }}/>)
        expect(screen.getByText("Never run")).toBeInTheDocument()
    })

})

describe('aggregate checkpoint', () => {

    const aggregate = {
        id: 'validation-stamp-pattern:12',
        type: 'validation-stamp-pattern',
        name: "CI-.*",
        data: {promotionLevelId: 12, include: "CI-.*", exclude: ""},
        members: [
            {
                id: 'validation-stamp:3',
                type: 'validation-stamp',
                name: "CI-BUILD",
                data: {validationStampId: 3, image: false},
                arrival: {
                    build: build("20260901-3"),
                    time: "2026-09-01T10:00:00",
                    status: {id: 'PASSED', name: "Passed", passed: true},
                },
            },
            {
                id: 'validation-stamp:4',
                type: 'validation-stamp',
                name: "CI-TEST",
                data: {validationStampId: 4, image: false},
            },
        ],
    }

    it('is labelled with the pattern, not with the stamps it stands for', () => {
        // What the configuration actually says: everything matching this, not these two things
        withEvents(<ValidationStampPatternCheckpoint checkpoint={aggregate}/>)
        expect(screen.getByText("CI-.*")).toBeInTheDocument()
    })

    it('summarises its members without expanding', () => {
        withEvents(<ValidationStampPatternCheckpoint checkpoint={aggregate}/>)
        expect(screen.getByText("1 of 2 passed")).toBeInTheDocument()
        expect(screen.queryByText("CI-BUILD")).not.toBeInTheDocument()
    })

    it('shows its members on demand, inside the node', () => {
        // Inside, never as new graph nodes: adding nodes would re-run the layout and reshuffle the
        // whole map under the user's cursor
        withEvents(<ValidationStampPatternCheckpoint checkpoint={aggregate}/>)
        fireEvent.click(screen.getByTestId('checkpoint-expand-validation-stamp-pattern:12'))
        expect(screen.getByText("CI-BUILD")).toBeInTheDocument()
        expect(screen.getByText("CI-TEST")).toBeInTheDocument()
    })

})

describe('slot checkpoint', () => {

    const slot = (data, arrival) => ({
        id: 'slot:abc',
        type: 'slot',
        name: "production",
        data: {slotId: 'abc', unreachable: false, otherBranch: null, ...data},
        arrival,
    })

    it('names the most recently deployed build', () => {
        withEvents(<SlotCheckpoint checkpoint={slot({}, {
            build: build("20260901-4"),
            time: "2026-09-01T10:00:00",
        })}/>)
        expect(screen.getByText("production")).toBeInTheDocument()
        expect(screen.getByText("20260901-4")).toBeInTheDocument()
    })

    it('says in words when nothing has ever been deployed there', () => {
        withEvents(<SlotCheckpoint checkpoint={slot({})}/>)
        expect(screen.getByText("Never deployed")).toBeInTheDocument()
    })

    it('names the branch when the deployed build belongs to another one', () => {
        // The deliberate exception to ADR 0007: a slot shows what is deployed, whatever its branch
        withEvents(<SlotCheckpoint checkpoint={slot({otherBranch: "release-1.3"}, {
            build: build("20260901-5"),
            time: "2026-09-01T10:00:00",
        })}/>)
        expect(screen.getByText("from release-1.3")).toBeInTheDocument()
    })

    it('says nothing about the branch when the deployed build is of this one', () => {
        withEvents(<SlotCheckpoint checkpoint={slot({}, {
            build: build("20260901-6"),
            time: "2026-09-01T10:00:00",
        })}/>)
        expect(screen.queryByText(/^from /)).not.toBeInTheDocument()
    })

    it('marks a slot this branch can never reach, and names no build for it', () => {
        // "You cannot get there from here" is the most important answer the map can give; omitting
        // the slot silently would leave someone wondering why production is missing
        withEvents(<SlotCheckpoint checkpoint={slot({unreachable: true})}/>)
        expect(screen.getByText("No build of this branch can be deployed here")).toBeInTheDocument()
        expect(screen.queryByText("Never deployed")).not.toBeInTheDocument()
    })

    it('links to the slot, where the admission rules can actually be read', () => {
        withEvents(<SlotCheckpoint checkpoint={slot({})}/>)
        expect(screen.getByText("production").closest('a')).toHaveAttribute(
            'href', '/extension/environments/slot/abc',
        )
    })

})

describe('unknown checkpoint', () => {

    it('still draws the name and the arrival of a kind it has never heard of', () => {
        // An extension contributes its own kinds; one unrecognised kind must not take the map with it
        withEvents(<UnknownCheckpoint checkpoint={{
            id: 'deployment-window:abc',
            type: 'deployment-window',
            name: "Friday freeze",
            arrival: {build: build("20260901-4"), time: "2026-09-01T10:00:00"},
        }}/>)
        expect(screen.getByText("Friday freeze")).toBeInTheDocument()
        expect(screen.getByText("20260901-4")).toBeInTheDocument()
    })

})
