import {renderHook, waitFor} from "@testing-library/react";
import {usePromotionLevel} from "@components/widgets/home/promotionChartUtils";
import {useValidationStampByName} from "@components/widgets/home/ValidationChartUtils";

const target = {
    id: "12",
    name: "GOLD",
    image: false,
    branch: {id: "3", name: "main", displayName: "main", project: {id: "1", name: "petclinic"}},
}

const respondWith = (body) => {
    global.fetch = jest.fn().mockResolvedValue({ok: true, status: 200, json: async () => body})
}

afterEach(() => {
    delete global.fetch
})

/**
 * The two hooks behind the chart widget titles resolve the configured entity by name.
 *
 * Both return the same shape - the loaded object, or `null`, and a `notFound` flag - so that the
 * widgets can tell the three states apart: still loading, loaded, and pointing at something which
 * does not exist. The first two used to be indistinguishable for the promotion level (its state
 * started on a truthy `{}`), and the third was invisible for both (#1694).
 */
describe.each([
    ['usePromotionLevel', usePromotionLevel, 'promotionLevelByName', 'promotionLevelObject'],
    ['useValidationStampByName', useValidationStampByName, 'validationStampByName', 'validationStampObject'],
])('%s', (_, useTarget, field, key) => {

    const renderTarget = (...args) => renderHook(() => useTarget(...args))

    it('has nothing loaded, and nothing missing, before the query answers', () => {
        global.fetch = jest.fn(() => new Promise(() => {
        }))
        const {result} = renderTarget("petclinic", "main", "GOLD")
        expect(result.current[key]).toBeNull()
        expect(result.current.notFound).toBe(false)
    })

    it('returns the loaded target', async () => {
        respondWith({[field]: target})
        const {result} = renderTarget("petclinic", "main", "GOLD")
        await waitFor(() => expect(result.current[key]).toEqual(target))
        expect(result.current.notFound).toBe(false)
    })

    it('flags the target as not found when the query resolves to nothing', async () => {
        respondWith({[field]: null})
        const {result} = renderTarget("petclinic", "main", "GOLD")
        await waitFor(() => expect(result.current.notFound).toBe(true))
        expect(result.current[key]).toBeNull()
    })

    it('does not mistake a failed query for a missing target', async () => {
        const consoleError = jest.spyOn(console, 'error').mockImplementation(() => {
        })
        try {
            global.fetch = jest.fn().mockResolvedValue({ok: false, status: 500, json: async () => ({})})
            const {result} = renderTarget("petclinic", "main", "GOLD")
            await waitFor(() => expect(global.fetch).toHaveBeenCalled())
            await new Promise(resolve => setTimeout(resolve, 20))
            expect(result.current[key]).toBeNull()
            expect(result.current.notFound).toBe(false)
        } finally {
            consoleError.mockRestore()
        }
    })

    it('does not report the new target as missing while it loads after a reconfiguration', async () => {
        // A not-found widget gets pointed at an existing target: the previous answer must not
        // stick to the new names while the new query is in flight
        respondWith({[field]: null})
        const {result, rerender} = renderHook(({name}) => useTarget("petclinic", "main", name), {
            initialProps: {name: "GOLD"},
        })
        await waitFor(() => expect(result.current.notFound).toBe(true))
        global.fetch = jest.fn(() => new Promise(() => {
        }))
        rerender({name: "SILVER"})
        await waitFor(() => expect(global.fetch).toHaveBeenCalled())
        expect(result.current.notFound).toBe(false)
        expect(result.current[key]).toBeNull()
    })

    it('exposes the error of a failed query', async () => {
        const consoleError = jest.spyOn(console, 'error').mockImplementation(() => {
        })
        try {
            global.fetch = jest.fn().mockResolvedValue({
                ok: false, status: 500,
                json: async () => ({error: {response: {errors: [{message: "Boom"}]}}}),
            })
            const {result} = renderTarget("petclinic", "main", "GOLD")
            await waitFor(() => expect(result.current.error).toBe("Boom"))
            expect(result.current.notFound).toBe(false)
        } finally {
            consoleError.mockRestore()
        }
    })

    it('stays idle while the configuration is incomplete', () => {
        respondWith({[field]: target})
        const {result} = renderTarget("petclinic", "main", undefined)
        expect(global.fetch).not.toHaveBeenCalled()
        expect(result.current[key]).toBeNull()
        expect(result.current.notFound).toBe(false)
    })
})
