import {prefixedFormName} from "@components/form/formUtils";

describe('prefixedFormName', () => {

    it('renders an array for a simple name', () => {
        const result = prefixedFormName('data', 'address')
        expect(result).toEqual(['data', 'address'])
    })

    it('renders an array for a name array', () => {
        const result = prefixedFormName('data', ['person', 'name'])
        expect(result).toEqual(['data', 'person', 'name'])
    })

})