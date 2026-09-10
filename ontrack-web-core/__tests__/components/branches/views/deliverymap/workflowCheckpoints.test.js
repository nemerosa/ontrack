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

import WorkflowCheckpoint from "@components/extension/workflows/deliverymap/WorkflowCheckpoint";
import SlotWorkflowCheckpoint from "@components/extension/environments/deliverymap/SlotWorkflowCheckpoint";
import {elapsedMs} from "@components/extension/workflows/deliverymap/workflowCheckpointRun";

const workflowCheckpoint = (data) => ({
    id: 'workflow:12:Canary verification',
    type: 'workflow',
    name: "Canary verification",
    data,
})

const slotWorkflowCheckpoint = (data) => ({
    id: 'slot-workflow:sw-1',
    type: 'slot-workflow',
    name: "Smoke tests",
    data,
})

describe('workflow checkpoint', () => {

    it('links its name to the run, so the node itself stays selectable and draggable', () => {
        render(<WorkflowCheckpoint checkpoint={workflowCheckpoint({
            workflowInstanceId: 'i-1',
            status: 'SUCCESS',
            startTime: "2026-09-01T10:00:00",
            durationMs: 2000,
        })}/>)
        expect(screen.getByRole('link', {name: "Canary verification"}))
            .toHaveAttribute('href', '/extension/workflows/instances/i-1')
    })

    it('says where the run got to', () => {
        render(<WorkflowCheckpoint checkpoint={workflowCheckpoint({
            workflowInstanceId: 'i-1',
            status: 'ERROR',
            startTime: "2026-09-01T10:00:00",
            durationMs: 2000,
        })}/>)
        expect(screen.getByText("Error")).toBeInTheDocument()
    })

    it('names no build of its own', () => {
        // Its build would always be the one its promotion level already names, one node to the left
        render(<WorkflowCheckpoint checkpoint={workflowCheckpoint({
            workflowInstanceId: 'i-1',
            status: 'SUCCESS',
            startTime: "2026-09-01T10:00:00",
            durationMs: 2000,
        })}/>)
        expect(screen.queryByTestId('checkpoint-lag')).not.toBeInTheDocument()
    })

})

describe('slot workflow checkpoint', () => {

    it('names the trigger which fires it', () => {
        // Which of the three moments of a deployment it belongs to is what decides whether it gates
        render(<SlotWorkflowCheckpoint checkpoint={slotWorkflowCheckpoint({
            slotWorkflowId: 'sw-1',
            trigger: 'CANDIDATE',
            workflowInstanceId: null,
            status: null,
        })}/>)
        expect(screen.getByTestId('slot-workflow-trigger')).toHaveTextContent("on candidate")
    })

    it('draws a workflow which has never run, and says so', () => {
        // A CANDIDATE workflow which never ran is the reason nothing ever deployed to that slot
        render(<SlotWorkflowCheckpoint checkpoint={slotWorkflowCheckpoint({
            slotWorkflowId: 'sw-1',
            trigger: 'CANDIDATE',
            workflowInstanceId: null,
            status: null,
        })}/>)
        expect(screen.getByText("Smoke tests")).toBeInTheDocument()
        expect(screen.getByText("Not started")).toBeInTheDocument()
        expect(screen.queryByRole('link')).not.toBeInTheDocument()
    })

    it('links to the run once there is one', () => {
        render(<SlotWorkflowCheckpoint checkpoint={slotWorkflowCheckpoint({
            slotWorkflowId: 'sw-1',
            trigger: 'RUNNING',
            workflowInstanceId: 'i-9',
            status: 'SUCCESS',
            startTime: "2026-09-01T10:00:00",
            durationMs: 2000,
        })}/>)
        expect(screen.getByRole('link', {name: "Smoke tests"}))
            .toHaveAttribute('href', '/extension/workflows/instances/i-9')
        expect(screen.getByText("Success")).toBeInTheDocument()
    })

    it('names a trigger it has never heard of rather than dropping it', () => {
        render(<SlotWorkflowCheckpoint checkpoint={slotWorkflowCheckpoint({
            slotWorkflowId: 'sw-1',
            trigger: 'CANCELLED',
            workflowInstanceId: null,
            status: null,
        })}/>)
        expect(screen.getByTestId('slot-workflow-trigger')).toHaveTextContent("CANCELLED")
    })

})

describe('how long a workflow has taken', () => {

    it('reports what the engine computed once the run is finished', () => {
        expect(elapsedMs({status: 'SUCCESS', startTime: "2026-09-01T10:00:00", durationMs: 2000}))
            .toBe(2000)
        expect(elapsedMs({status: 'ERROR', startTime: "2026-09-01T10:00:00", durationMs: 500}))
            .toBe(500)
    })

    it('counts from the start while the run is still going', () => {
        // `durationMs` is 0 until the LAST node ends, which is why every other workflow UI hides it
        expect(elapsedMs(
            {status: 'RUNNING', startTime: "2026-09-01T10:00:00", durationMs: 0},
            "2026-09-01T10:00:30",
        )).toBe(30000)
        expect(elapsedMs(
            {status: 'STARTED', startTime: "2026-09-01T10:00:00", durationMs: 0},
            "2026-09-01T10:00:05",
        )).toBe(5000)
    })

    it('never reports a negative age when the two clocks disagree', () => {
        expect(elapsedMs(
            {status: 'RUNNING', startTime: "2026-09-01T10:00:10", durationMs: 0},
            "2026-09-01T10:00:00",
        )).toBe(0)
    })

    it('has nothing to say about a run which never started', () => {
        expect(elapsedMs({status: null, startTime: null, durationMs: null})).toBeNull()
        expect(elapsedMs({status: 'STARTED', startTime: null, durationMs: 0})).toBeNull()
    })

})
