import {
    applyValidationStampFilter,
    CHECKPOINT_NODE_TYPE,
    hasNoDependencies,
    isMapEmpty,
    toFlowEdges,
    toFlowNodes,
    withEdgeColor,
} from "@components/branches/views/deliverymap/deliveryMapModel"

describe('delivery map model', () => {

    describe('nodes', () => {

        it('uses the checkpoint id as the node id, unchanged', () => {
            // #1707 keeps node positions across a refresh, and React Flow can only do that when it
            // sees the same node ids
            const nodes = toFlowNodes([
                {id: 'promotion-level:12', type: 'promotion-level', name: "BRONZE"},
            ])
            expect(nodes[0].id).toBe('promotion-level:12')
        })

        it('gives every checkpoint the same node type, whatever its kind', () => {
            // The kind is data, dispatched inside the node, so an unknown kind still lays out
            const nodes = toFlowNodes([
                {id: 'promotion-level:12', type: 'promotion-level'},
                {id: 'slot:abc', type: 'slot'},
            ])
            expect(nodes.map(it => it.type)).toEqual([CHECKPOINT_NODE_TYPE, CHECKPOINT_NODE_TYPE])
        })

        it('carries the whole checkpoint as node data', () => {
            const checkpoint = {id: 'validation-stamp:3', type: 'validation-stamp', name: "QUALITY"}
            expect(toFlowNodes([checkpoint])[0].data.checkpoint).toBe(checkpoint)
        })

        it('gives a placeholder position for the layout to overwrite', () => {
            expect(toFlowNodes([{id: 'a'}])[0].position).toEqual({x: 0, y: 0})
        })

        it('maps nothing when there is nothing', () => {
            expect(toFlowNodes()).toEqual([])
            expect(toFlowNodes([])).toEqual([])
        })

    })

    describe('edges', () => {

        const unlocks = {
            id: 'unlocks:validation-stamp:3->promotion-level:12',
            kind: 'UNLOCKS',
            source: 'validation-stamp:3',
            target: 'promotion-level:12',
        }

        const requires = {
            id: 'requires:promotion-level:11->promotion-level:12',
            kind: 'REQUIRES',
            source: 'promotion-level:11',
            target: 'promotion-level:12',
        }

        it('keeps the edge id, source and target', () => {
            const edge = toFlowEdges([unlocks])[0]
            expect(edge.id).toBe(unlocks.id)
            expect(edge.source).toBe('validation-stamp:3')
            expect(edge.target).toBe('promotion-level:12')
        })

        it('labels each edge in words, read along the arrow', () => {
            // Every edge runs from the prerequisite to what depends on it, and the label sits on the
            // arrow, so it has to be true read from the SOURCE. "requires" is only true read the
            // other way: `SILVER requires GOLD` says the opposite of what the edge means.
            expect(toFlowEdges([unlocks])[0].label).toBe("unlocks")
            expect(toFlowEdges([requires])[0].label).toBe("required by")
        })

        it('draws a requires edge dashed, because it constrains rather than acts', () => {
            // The dash survives greyscale, so the distinction does not rest on the label alone
            expect(toFlowEdges([requires])[0].style.strokeDasharray).toBeTruthy()
            expect(toFlowEdges([unlocks])[0].style.strokeDasharray).toBeUndefined()
        })

        it('points every edge at its target, with an arrowhead big enough to see', () => {
            const edge = toFlowEdges([unlocks])[0]
            expect(edge.markerEnd).toBeTruthy()
            // React Flow leaves SVG's `markerUnits` at `strokeWidth`, so what it is given is a
            // multiple of the line width. The rendered head is that times the stroke, and has to
            // stay bigger than React Flow's own 16px default whatever the stroke becomes.
            expect(edge.markerEnd.width * edge.style.strokeWidth).toBeGreaterThan(16)
        })

        it('leaves the colour out, because it belongs to the theme and not to the kind', () => {
            const edge = toFlowEdges([unlocks])[0]
            expect(edge.style.stroke).toBeUndefined()
            expect(edge.markerEnd.color).toBeUndefined()
        })

        it('names an edge kind it has never heard of instead of calling it unlocks', () => {
            // The kinds are a CLOSED set on the server, so this is not the open registry the
            // checkpoint kinds have. It matters anyway: calling an unknown kind "unlocks" would
            // claim a configuration ACTS, which is the one error the two kinds exist to keep apart.
            const edge = toFlowEdges([{...requires, kind: 'SUPERSEDES'}])[0]
            expect(edge.label).toBe('SUPERSEDES')
            expect(edge.style.strokeDasharray).toBeUndefined()
        })

        it('paints the line and its arrowhead in one colour', () => {
            // An arrowhead in another shade reads as a separate mark rather than as the end of the
            // line it belongs to
            const [edge] = withEdgeColor(toFlowEdges([unlocks]), '#123456')
            expect(edge.style.stroke).toBe('#123456')
            expect(edge.markerEnd.color).toBe('#123456')
        })

        it('keeps everything else of an edge when painting it', () => {
            // Painting happens at render, on edges the layout has already been computed from
            const [edge] = withEdgeColor(toFlowEdges([requires]), '#123456')
            expect(edge.label).toBe("required by")
            expect(edge.style.strokeDasharray).toBeTruthy()
            expect(edge.markerEnd.type).toBe(toFlowEdges([requires])[0].markerEnd.type)
        })

        it('maps nothing when there is nothing', () => {
            expect(toFlowEdges()).toEqual([])
            expect(toFlowEdges([])).toEqual([])
        })

    })

    describe('empty states', () => {

        it('is empty when the branch has no checkpoint at all', () => {
            expect(isMapEmpty({checkpoints: [], edges: []})).toBe(true)
            expect(isMapEmpty(null)).toBe(true)
            expect(isMapEmpty(undefined)).toBe(true)
        })

        it('is not empty as soon as there is one checkpoint', () => {
            expect(isMapEmpty({checkpoints: [{id: 'a'}], edges: []})).toBe(false)
        })

        it('has no dependencies when there are checkpoints but nothing joins them', () => {
            // Promotion levels with no auto promotion and no promotion dependency anywhere
            expect(hasNoDependencies({checkpoints: [{id: 'a'}], edges: []})).toBe(true)
        })

        it('has dependencies as soon as one edge is drawn', () => {
            expect(hasNoDependencies({checkpoints: [{id: 'a'}], edges: [{id: 'e'}]})).toBe(false)
        })

        it('does not claim a branch with nothing on it merely lacks dependencies', () => {
            // The two states get two different explanations, so they must not both fire
            expect(hasNoDependencies({checkpoints: [], edges: []})).toBe(false)
        })

    })

})

describe('validation stamp filter on a delivery map', () => {

    const promotion = {id: 'promotion-level:12', type: 'promotion-level', name: "SILVER"}
    const quality = {id: 'validation-stamp:1', type: 'validation-stamp', name: "QUALITY"}
    const security = {id: 'validation-stamp:2', type: 'validation-stamp', name: "SECURITY"}
    const aggregate = {
        id: 'validation-stamp-pattern:12',
        type: 'validation-stamp-pattern',
        name: "CI-.*",
        members: [
            {id: 'validation-stamp:3', type: 'validation-stamp', name: "CI-BUILD"},
            {id: 'validation-stamp:4', type: 'validation-stamp', name: "CI-TEST"},
        ],
    }

    const edge = (source) => ({
        id: `unlocks:${source}->promotion-level:12`,
        kind: 'UNLOCKS',
        source,
        target: 'promotion-level:12',
    })

    const map = {
        checkpoints: [promotion, quality, security, aggregate],
        edges: [edge(quality.id), edge(security.id), edge(aggregate.id)],
    }

    it('leaves the map alone when no filter is selected', () => {
        expect(applyValidationStampFilter(map, undefined)).toBe(map)
        expect(applyValidationStampFilter(map, {})).toBe(map)
    })

    it('keeps only the validation stamps the filter names', () => {
        const filtered = applyValidationStampFilter(map, {vsNames: ["QUALITY"]})
        expect(filtered.checkpoints.map(it => it.name)).toEqual(["SILVER", "QUALITY"])
    })

    it('never hides a promotion level, whatever the filter says', () => {
        // A filter is a statement about stamps; hiding a promotion because of one is a different claim
        const filtered = applyValidationStampFilter(map, {vsNames: []})
        expect(filtered.checkpoints.map(it => it.name)).toEqual(["SILVER"])
    })

    it('narrows an aggregate checkpoint through its members', () => {
        const filtered = applyValidationStampFilter(map, {vsNames: ["CI-BUILD"]})
        const kept = filtered.checkpoints.find(it => it.type === 'validation-stamp-pattern')
        expect(kept.members.map(it => it.name)).toEqual(["CI-BUILD"])
    })

    it('drops an aggregate the filter leaves standing for nothing', () => {
        // An aggregate is its members; one standing for none of them says nothing at all
        const filtered = applyValidationStampFilter(map, {vsNames: ["QUALITY"]})
        expect(filtered.checkpoints.find(it => it.type === 'validation-stamp-pattern')).toBeUndefined()
        expect(filtered.edges.map(it => it.source)).not.toContain('validation-stamp-pattern:12')
    })

    it('drops the edges of the checkpoints it removed', () => {
        // A line to nowhere reads as a broken map rather than as a hidden checkpoint
        const filtered = applyValidationStampFilter(map, {vsNames: ["QUALITY"]})
        expect(filtered.edges.map(it => it.source)).toEqual(['validation-stamp:1'])
    })

    it('does not mutate the map it was given', () => {
        applyValidationStampFilter(map, {vsNames: ["QUALITY"]})
        expect(map.checkpoints).toHaveLength(4)
        expect(aggregate.members).toHaveLength(2)
    })

})
