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

import {
    applyVisibility,
    visibilityKinds,
    isShown,
} from "@components/branches/views/deliverymap/visibilityKinds";
import DeliveryMapVisibility from "@components/branches/views/deliverymap/DeliveryMapVisibility";

describe('what can be taken off a delivery map', () => {

    it('names every entry, labels it and says how to narrow the map by it', () => {
        // The list is what the toolbar draws itself from, and what the view folds over the map:
        // adding a kind is an entry here and nothing else
        visibilityKinds.forEach(entry => {
            expect(entry.id).toBeTruthy()
            expect(entry.label).toBeTruthy()
            expect(entry.icon).toBeTruthy()
            expect(typeof entry.apply).toBe('function')
        })
    })

    it('gives every entry an id of its own', () => {
        const ids = visibilityKinds.map(it => it.id)
        expect(new Set(ids).size).toBe(ids.length)
    })

    it('shows what no preference mentions', () => {
        // What is turned OFF is stored, so a kind added later is drawn for everyone rather than
        // hidden from every reader with a preference already stored
        expect(isShown(undefined, 'validation-stamps')).toBe(true)
        expect(isShown({}, 'validation-stamps')).toBe(true)
        expect(isShown({'slots': false}, 'validation-stamps')).toBe(true)
    })

    it('hides what the reader turned off', () => {
        expect(isShown({'validation-stamps': false}, 'validation-stamps')).toBe(false)
    })

})

describe('narrowing a delivery map by what is shown', () => {

    const promotion = {id: 'promotion-level:12', type: 'promotion-level', name: "SILVER"}
    const stamp = {id: 'validation-stamp:1', type: 'validation-stamp', name: "QUALITY"}
    const workflow = {id: 'workflow:12:Canary', type: 'workflow', name: "Canary"}
    const slotWorkflow = {id: 'slot-workflow:sw-1', type: 'slot-workflow', name: "Smoke tests"}
    const map = {
        checkpoints: [promotion, stamp, workflow, slotWorkflow],
        edges: [{id: 'e', kind: 'UNLOCKS', source: stamp.id, target: promotion.id}],
    }

    it('leaves the map alone when nothing is turned off', () => {
        expect(applyVisibility(map, {})).toBe(map)
    })

    it('takes the validation stamps off when they are turned off', () => {
        const narrowed = applyVisibility(map, {'validation-stamps': false})
        expect(narrowed.checkpoints.map(it => it.type))
            .toEqual(['promotion-level', 'workflow', 'slot-workflow'])
        expect(narrowed.edges).toEqual([])
    })

    it('takes both kinds of workflow off with the one entry', () => {
        const narrowed = applyVisibility(map, {'workflows': false})
        expect(narrowed.checkpoints.map(it => it.type))
            .toEqual(['promotion-level', 'validation-stamp'])
    })

    it('folds every entry over the map, one after the other', () => {
        const narrowed = applyVisibility(map, {'validation-stamps': false, 'workflows': false})
        expect(narrowed.checkpoints.map(it => it.type)).toEqual(['promotion-level'])
    })

    it('has nothing to narrow before the map arrives', () => {
        expect(applyVisibility(null, {'validation-stamps': false})).toBeNull()
    })

})

describe('the visibility toolbar', () => {

    it('names in words what each toggle draws, not just in an icon', () => {
        // Three unlabelled eyes stacked in the graph's corner say nothing about what each hides
        render(<DeliveryMapVisibility visibility={{}} onToggle={jest.fn()}/>)
        visibilityKinds.forEach(entry => {
            expect(screen.getByText(entry.label)).toBeInTheDocument()
        })
    })

    it('shows each entry as checked until it is turned off', () => {
        const {rerender} = render(<DeliveryMapVisibility visibility={{}} onToggle={jest.fn()}/>)
        // antd puts any extra property on the input itself, which is the checkbox to assert on
        const stamps = () => screen.getByTestId('delivery-map-show-validation-stamps')
        expect(stamps()).toBeChecked()
        rerender(<DeliveryMapVisibility visibility={{'validation-stamps': false}} onToggle={jest.fn()}/>)
        expect(stamps()).not.toBeChecked()
    })

    it('says which entry was toggled, and to what', () => {
        const onToggle = jest.fn()
        render(<DeliveryMapVisibility visibility={{}} onToggle={onToggle}/>)
        fireEvent.click(screen.getByTestId('delivery-map-show-validation-stamps'))
        expect(onToggle).toHaveBeenCalledWith('validation-stamps', false)
    })

})
