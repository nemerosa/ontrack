import Duration, {formatSeconds} from "@components/common/Duration";
import {render, screen} from "@testing-library/react";
import React from "react";
import '@testing-library/jest-dom';

describe('Duration', () => {

    it('formatSeconds returns a - string by default when seconds is undefined', () => {
        const text = formatSeconds(undefined)
        expect(text).toEqual('-')
    })

    it('formatSeconds returns a - string by default when seconds is empty', () => {
        const text = formatSeconds('')
        expect(text).toEqual('-')
    })

    it('formatSeconds returns a - string by default when seconds is blank', () => {
        const text = formatSeconds(' ')
        expect(text).toEqual('-')
    })

    it('formatSeconds returns a custom string when seconds is undefined', () => {
        const text = formatSeconds(undefined, "n/a")
        expect(text).toEqual('n/a')
    })

    it('formatSeconds returns 1 second for one second', () => {
        const text = formatSeconds(1)
        expect(text).toEqual('1 second')
    })

    it('formatSeconds returns N seconds for less than 60 seconds', () => {
        const text = formatSeconds(50)
        expect(text).toEqual('50 seconds')
    })

    it('formatSeconds humanizes the rendering for more than 60 seconds', () => {
        const text = formatSeconds(300)
        expect(text).toEqual('5 minutes')
    })

    it('renders default text when `seconds` is null or undefined', () => {
        const defaultText = 'No duration available';
        render(<Duration defaultText={defaultText}/>);
        expect(screen.getByText(defaultText)).toBeInTheDocument();
    });

    it('renders humanized duration for seconds less than 60', () => {
        render(<Duration seconds={45}/>);
        expect(screen.getByText('45 seconds')).toBeInTheDocument();
    });

    it('renders without a tooltip and includes seconds text when `displaySecondsInTooltip` is false', () => {
        render(<Duration seconds={120} displaySecondsInTooltip={false}/>);
        expect(screen.getByText('2 minutes (120 seconds)')).toBeInTheDocument();
    });

    it('renders only humanized text when `displaySeconds` is false', () => {
        render(<Duration seconds={300} displaySeconds={false}/>);
        expect(screen.getByText('5 minutes')).toBeInTheDocument();
    });

    it('renders 0 second for 0', () => {
        render(<Duration
            seconds={0}
            displaySeconds={true}
        />);
        expect(screen.getByText('0 second')).toBeInTheDocument();
    });

    it('calls `formatSeconds` correctly', () => {
        // Ensure that the formatSeconds helper works correctly independent of the component
        expect(formatSeconds(30)).toBe('30 seconds');
        expect(formatSeconds(3600)).toBe('an hour');
        expect(formatSeconds(0, '-')).toBe('0 second'); // Handling invalid number
        expect(formatSeconds(-1, '-')).toBe('-'); // Handling invalid number
    });

})