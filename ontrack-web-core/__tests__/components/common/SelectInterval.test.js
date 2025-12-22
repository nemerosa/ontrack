import '@testing-library/jest-dom';
import {toMilliSeconds} from "@components/common/SelectInterval";

describe('SelectInterval logic', () => {
    it('toMilliSeconds for minutes', () => {
        expect(toMilliSeconds({count: 2, unit: 'M'})).toBe(2 * 60 * 1000);
    });

    it('toMilliSeconds for hours', () => {
        expect(toMilliSeconds({count: 3, unit: 'H'})).toBe(3 * 3600 * 1000);
    });

    it('toMilliSeconds for days', () => {
        expect(toMilliSeconds({count: 1, unit: 'D'})).toBe(24 * 3600 * 1000);
    });
});
