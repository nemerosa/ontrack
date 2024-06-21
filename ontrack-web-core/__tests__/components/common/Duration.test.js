import {formatSeconds} from "@components/common/Duration";

describe('Duration', () => {

    it('formatSeconds returns a - string by default when seconds is undefined', () => {
        const text = formatSeconds(undefined)
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

})