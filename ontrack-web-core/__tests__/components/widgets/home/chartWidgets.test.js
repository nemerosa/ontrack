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

// The widgets resolve their target through a hook; the tests drive its three states directly
const mockUsePromotionLevel = jest.fn()
const mockUseValidationStampByName = jest.fn()
jest.mock("../../../../components/widgets/home/promotionChartUtils", () => ({
    usePromotionLevel: (...args) => mockUsePromotionLevel(...args),
}))
jest.mock("../../../../components/widgets/home/ValidationChartUtils", () => ({
    useValidationStampByName: (...args) => mockUseValidationStampByName(...args),
}))

// The charts would only pull in an ECharts canvas jsdom cannot draw. `jest.mock` is hoisted above
// any `const`, so each factory is written out in full.
jest.mock("../../../../components/promotionLevels/PromotionLevelLeadTimeChart",
    () => () => require('react').createElement('div', {'data-testid': 'chart'}))
jest.mock("../../../../components/promotionLevels/PromotionLevelFrequencyChart",
    () => () => require('react').createElement('div', {'data-testid': 'chart'}))
jest.mock("../../../../components/promotionLevels/PromotionLevelStabilityChart",
    () => () => require('react').createElement('div', {'data-testid': 'chart'}))
jest.mock("../../../../components/promotionLevels/PromotionLevelTTRChart",
    () => () => require('react').createElement('div', {'data-testid': 'chart'}))
jest.mock("../../../../components/validationStamps/ValidationStampStabilityChart",
    () => () => require('react').createElement('div', {'data-testid': 'chart'}))
jest.mock("../../../../components/validationStamps/ValidationStampMetricsChart",
    () => () => require('react').createElement('div', {'data-testid': 'chart'}))

import {EventsContext} from "@components/common/EventsContext";
import {DashboardWidgetCellContext} from "@components/dashboards/DashboardWidgetCellContextProvider";
import PromotionLeadTimeChartWidget from "@components/widgets/home/PromotionLeadTimeChartWidget";
import PromotionFrequencyChartWidget from "@components/widgets/home/PromotionFrequencyChartWidget";
import PromotionStabilityChartWidget from "@components/widgets/home/PromotionStabilityChartWidget";
import PromotionTTRChartWidget from "@components/widgets/home/PromotionTTRChartWidget";
import ValidationStabilityChartWidget from "@components/widgets/home/ValidationStabilityChartWidget";
import ValidationMetricsChartWidget from "@components/widgets/home/ValidationMetricsChartWidget";

beforeEach(() => {
    // The entity icon fetches its image on mount
    global.fetch = jest.fn().mockResolvedValue({
        ok: true,
        json: () => Promise.resolve({dataURL: 'data:image/png;base64,AAAA'}),
    })
    mockUsePromotionLevel.mockReset()
    mockUseValidationStampByName.mockReset()
})

const branch = {id: "3", name: "main", displayName: "main", project: {id: "1", name: "petclinic"}}

const withContexts = (setTitle, element) => (
    <EventsContext.Provider value={{fireEvent: jest.fn(), subscribeToEvent: jest.fn()}}>
        <DashboardWidgetCellContext.Provider value={{setTitle}}>
            {element}
        </DashboardWidgetCellContext.Provider>
    </EventsContext.Provider>
)

/**
 * Renders a widget and returns the title it handed to the dashboard cell, rendered on its own.
 *
 * The title is not part of the widget's output: it goes to the cell through the context, so the
 * test captures the last one and renders it in a second container.
 */
const renderWidget = (element) => {
    const setTitle = jest.fn()
    const body = render(withContexts(setTitle, element))
    const title = setTitle.mock.calls.length > 0 ?
        render(withContexts(jest.fn(), setTitle.mock.calls.at(-1)[0])) :
        null
    return {body, title, setTitle}
}

const promotionProps = {project: "petclinic", branch: "main", promotionLevel: "GOLD", interval: "3m", period: "1w"}
const validationProps = {
    project: "petclinic",
    branch: "main",
    validationStamp: "UNIT.TESTS",
    interval: "3m",
    period: "1w"
}

/**
 * The single-target chart widgets: four on a promotion level, two on a validation stamp.
 *
 * Each one has three states to show, and the title must tell them apart (#1694):
 *
 * - loading: the configured names, so the user never sees an empty entity;
 * - loaded: the linked entity, tested in `chartTitles.test.js`;
 * - not found: the configured names again, marked as such, with the body explaining what is
 *   missing and where to fix it - instead of a title stuck on "Loading..." above an empty chart.
 */
describe('PromotionLeadTimeChartWidget', () => {

    it('names the configured target while it is loading', () => {
        mockUsePromotionLevel.mockReturnValue({promotionLevelObject: null, notFound: false})
        const {body, title} = renderWidget(<PromotionLeadTimeChartWidget {...promotionProps}/>)
        expect(title).not.toBeNull()
        expect(title.container).toHaveTextContent(/Lead time to\s*GOLD\s*on\s*main@petclinic/)
        expect(title.container).not.toHaveTextContent('(not found)')
        expect(title.container.querySelectorAll('a')).toHaveLength(0)
        expect(body.container.querySelector('[data-testid="chart"]')).toBeNull()
        expect(body.container.querySelector('.ant-alert')).toBeNull()
    })

    it('links the loaded target and draws the chart', () => {
        mockUsePromotionLevel.mockReturnValue({
            promotionLevelObject: {id: "12", name: "GOLD", image: false, branch},
            notFound: false,
        })
        const {body, title} = renderWidget(<PromotionLeadTimeChartWidget {...promotionProps}/>)
        expect(title.container.querySelector('a[href="/promotionLevel/12"]')).not.toBeNull()
        expect(title.container).not.toHaveTextContent('(not found)')
        expect(body.container.querySelector('[data-testid="chart"]')).not.toBeNull()
    })

    it('says which target is missing instead of staying on an empty chart', () => {
        mockUsePromotionLevel.mockReturnValue({promotionLevelObject: null, notFound: true})
        const {body, title} = renderWidget(<PromotionLeadTimeChartWidget {...promotionProps}/>)
        expect(title.container).toHaveTextContent(/Lead time to\s*GOLD\s*on\s*main@petclinic\s*\(not found\)/)
        expect(title.container.querySelectorAll('a')).toHaveLength(0)
        expect(body.container.querySelector('[data-testid="chart"]')).toBeNull()
        expect(screen.getByRole('alert')).toHaveTextContent(
            'Promotion level GOLD does not exist on branch main of project petclinic.'
        )
        expect(screen.getByRole('alert')).toHaveTextContent('Edit the dashboard to reconfigure this widget.')
    })

    it('shows the error of a failed query instead of an empty body', () => {
        mockUsePromotionLevel.mockReturnValue({promotionLevelObject: null, notFound: false, error: "Boom"})
        const {body, title} = renderWidget(<PromotionLeadTimeChartWidget {...promotionProps}/>)
        expect(title.container).not.toHaveTextContent('(not found)')
        expect(body.container.querySelector('[data-testid="chart"]')).toBeNull()
        expect(screen.getByRole('alert')).toHaveTextContent('Boom')
    })

    it('keeps the chart options in the title in every state', () => {
        mockUsePromotionLevel.mockReturnValue({promotionLevelObject: null, notFound: true})
        const {title} = renderWidget(<PromotionLeadTimeChartWidget {...promotionProps}/>)
        expect(title.container).toHaveTextContent(/3m/)
    })
})

describe.each([
    {
        widget: 'PromotionFrequencyChartWidget', Widget: PromotionFrequencyChartWidget,
        mockHook: mockUsePromotionLevel, key: 'promotionLevelObject', props: promotionProps,
        prefix: 'Frequency of', entity: 'Promotion level', name: 'GOLD',
    },
    {
        widget: 'PromotionStabilityChartWidget', Widget: PromotionStabilityChartWidget,
        mockHook: mockUsePromotionLevel, key: 'promotionLevelObject', props: promotionProps,
        prefix: 'Stability of', entity: 'Promotion level', name: 'GOLD',
    },
    {
        widget: 'PromotionTTRChartWidget', Widget: PromotionTTRChartWidget,
        mockHook: mockUsePromotionLevel, key: 'promotionLevelObject', props: promotionProps,
        prefix: 'TTR to', entity: 'Promotion level', name: 'GOLD',
    },
    {
        widget: 'ValidationStabilityChartWidget', Widget: ValidationStabilityChartWidget,
        mockHook: mockUseValidationStampByName, key: 'validationStampObject', props: validationProps,
        prefix: 'Stability of', entity: 'Validation stamp', name: 'UNIT.TESTS',
    },
    {
        widget: 'ValidationMetricsChartWidget', Widget: ValidationMetricsChartWidget,
        mockHook: mockUseValidationStampByName, key: 'validationStampObject', props: validationProps,
        prefix: 'Metrics of', entity: 'Validation stamp', name: 'UNIT.TESTS',
    },
])('$widget', ({Widget, mockHook, key, props, prefix, entity, name}) => {

    it('names the configured target while it is loading', () => {
        mockHook.mockReturnValue({[key]: null, notFound: false})
        const {title} = renderWidget(<Widget {...props}/>)
        expect(title).not.toBeNull()
        // The title pieces are laid out by an antd `Space`, with no whitespace between them
        const escaped = name.replace('.', '\\.')
        expect(title.container).toHaveTextContent(new RegExp(`${prefix}\\s*${escaped}\\s*on\\s*main@petclinic`))
        expect(title.container).not.toHaveTextContent('(not found)')
    })

    it('says which target is missing', () => {
        mockHook.mockReturnValue({[key]: null, notFound: true})
        const {body, title} = renderWidget(<Widget {...props}/>)
        expect(title.container).toHaveTextContent('(not found)')
        expect(body.container.querySelector('[data-testid="chart"]')).toBeNull()
        expect(screen.getByRole('alert')).toHaveTextContent(
            `${entity} ${name} does not exist on branch main of project petclinic.`
        )
    })
})
