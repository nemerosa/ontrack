import {generateInitials} from "@components/common/icons/GeneratedIcon";

describe('GeneratedIcon', () => {
    it('name initials', () => {
        expect(generateInitials("acceptance")).toEqual('AC')
        expect(generateInitials("staging-live")).toEqual('SL')
        expect(generateInitials("acceptance-pilot")).toEqual('AP')
        expect(generateInitials("production-live")).toEqual('PL')
        expect(generateInitials("production")).toEqual('PR')
    })
})
