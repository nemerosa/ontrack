import {checkpointTypes, getCheckpointType} from "@components/branches/views/deliverymap/checkpointTypes"
import UnknownCheckpoint from "@components/branches/views/deliverymap/checkpoints/UnknownCheckpoint"
import {summariseMembers} from "@components/branches/views/deliverymap/checkpoints/aggregateSummary"

// The renderers pull the whole entity-icon tree in; only the registry is under test here
jest.mock("../../../../../components/branches/views/deliverymap/checkpoints/PromotionLevelCheckpoint", () => () => <div/>)
jest.mock("../../../../../components/branches/views/deliverymap/checkpoints/ValidationStampCheckpoint", () => () => <div/>)
jest.mock("../../../../../components/branches/views/deliverymap/checkpoints/ValidationStampPatternCheckpoint", () => () => <div/>)
jest.mock("../../../../../components/extension/environments/deliverymap/SlotCheckpoint", () => () => <div/>)
jest.mock("../../../../../components/extension/workflows/deliverymap/WorkflowCheckpoint", () => () => <div/>)
jest.mock("../../../../../components/extension/environments/deliverymap/SlotWorkflowCheckpoint", () => () => <div/>)

describe('checkpoint type registry', () => {

    it('registers the kinds shipped with the product, core and extension alike', () => {
        expect(Object.keys(checkpointTypes)).toEqual([
            'promotion-level',
            'validation-stamp',
            'validation-stamp-pattern',
            'slot',
            'workflow',
            'slot-workflow',
            'unresolved',
        ])
    })

    it('gives every kind a component and a size for the layout to reserve', () => {
        // elk needs the sizes BEFORE anything is rendered, so they belong to the kind
        Object.values(checkpointTypes).forEach(entry => {
            expect(entry.component).toBeTruthy()
            expect(typeof entry.width).toBe('number')
            expect(typeof entry.height).toBe('number')
        })
    })

    it('resolves a registered kind', () => {
        expect(getCheckpointType('promotion-level')).toBe(checkpointTypes['promotion-level'])
    })

    it.each([
        ['a kind no shipped extension contributes', 'deployment-window'],
        ['an undefined kind', undefined],
        ['an empty kind', ''],
    ])('falls back rather than failing for %s', (_, type) => {
        // The set of kinds is open on the server, and this frontend may be older than it
        const entry = getCheckpointType(type)
        expect(entry.component).toBe(UnknownCheckpoint)
        expect(typeof entry.width).toBe('number')
        expect(typeof entry.height).toBe('number')
    })

})

describe('aggregate summary', () => {

    const member = (statusId, passed) => ({
        id: `validation-stamp:${statusId ?? 'none'}`,
        arrival: statusId ? {status: {id: statusId, passed}} : undefined,
    })

    it('counts the members whose latest run passed', () => {
        const summary = summariseMembers([
            member('PASSED', true),
            member('FAILED', false),
            member('PASSED', true),
        ])
        expect(summary).toEqual({total: 3, passed: 2, run: 3})
    })

    it('does not count a member which never ran as failing', () => {
        // Not having arrived is a different thing from having arrived and failed
        const summary = summariseMembers([member('PASSED', true), member(null)])
        expect(summary).toEqual({total: 2, passed: 1, run: 1})
    })

    it('summarises nothing as nothing', () => {
        expect(summariseMembers()).toEqual({total: 0, passed: 0, run: 0})
        expect(summariseMembers([])).toEqual({total: 0, passed: 0, run: 0})
    })

})
