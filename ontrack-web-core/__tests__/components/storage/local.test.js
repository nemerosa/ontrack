import {
    getLocalDeliveryMapValidationStamps,
    setLocalDeliveryMapValidationStamps,
} from "@components/storage/local";

describe('delivery map validation stamp preference', () => {

    beforeEach(() => {
        localStorage.clear()
    })

    it('shows the validation stamps until the user says otherwise', () => {
        // Off by default would make the map's first impression a chain of promotions with no
        // visible cause
        expect(getLocalDeliveryMapValidationStamps()).toBe(true)
    })

    it('remembers that they were hidden', () => {
        setLocalDeliveryMapValidationStamps(false)
        expect(getLocalDeliveryMapValidationStamps()).toBe(false)
    })

    it('remembers that they were shown again', () => {
        setLocalDeliveryMapValidationStamps(false)
        setLocalDeliveryMapValidationStamps(true)
        expect(getLocalDeliveryMapValidationStamps()).toBe(true)
    })

    it('shows them for a value it does not recognise', () => {
        // A corrupted entry must not leave the reader looking at a map missing half its vocabulary
        localStorage.setItem('delivery-map-validation-stamps', 'perhaps')
        expect(getLocalDeliveryMapValidationStamps()).toBe(true)
    })

})
