import {MarkerType} from "reactflow"

/**
 * Turning the delivery map the server sends into what React Flow draws.
 *
 * Kept as plain functions, away from any component, because everything here is a pure mapping and
 * because #1707 - keeping node positions across a refresh - hangs on the node ids being exactly the
 * checkpoint ids, which is a thing worth testing on its own.
 */

/**
 * The single React Flow node type. One type for every checkpoint kind: the kind is data, dispatched
 * inside the node by `checkpointTypes`, so that a kind the frontend has never heard of still lays
 * out and still draws something.
 */
export const CHECKPOINT_NODE_TYPE = 'checkpoint'

/**
 * Builds the React Flow nodes of a list of checkpoints.
 *
 * The node id IS the checkpoint id, never a generated one. React Flow keeps a node's position across
 * a refresh only when it sees the same id; a regenerated id remounts the node and loses its place.
 *
 * The position is a placeholder: `autoLayout` overwrites it with elk's answer.
 */
export function toFlowNodes(checkpoints = []) {
    return checkpoints.map(checkpoint => ({
        id: checkpoint.id,
        type: CHECKPOINT_NODE_TYPE,
        position: {x: 0, y: 0},
        data: {checkpoint},
    }))
}

/**
 * How wide a line is drawn, and how big its arrowhead is.
 *
 * Which end an edge points at IS the content of a dependency, so the arrowhead is not decoration and
 * is sized to be seen. React Flow's own defaults - a hairline and a 16px marker in its default grey -
 * left the head all but invisible against the line (#1717).
 *
 * The marker is expressed in PIXELS here and divided below, because React Flow leaves SVG's
 * `markerUnits` at `strokeWidth`: what it is given is a multiple of the line's width, not a size.
 * Written the other way round, changing [EDGE_STROKE_WIDTH] would silently rescale every arrowhead.
 */
const EDGE_STROKE_WIDTH = 1.5
const EDGE_MARKER_PX = 33

/**
 * How each edge kind is drawn, keyed by the kind's own name.
 *
 * `DeliveryMapEdgeKind` is a CLOSED set on the server - unlike the set of checkpoint kinds - so this
 * is not the open registry `checkpointTypes` is, and a kind missing from here is a model change
 * rather than an extension. The fallback below exists for a narrower reason: labelling an unknown
 * kind "unlocks" would claim that a configuration ACTS when nothing here knows whether it does, and
 * that is the one error `DeliveryMapEdgeKind` was split in two to prevent.
 *
 * @property label What the edge says, read ALONG the arrow - see `toFlowEdges`
 * @property strokeDasharray Dashed for the kind which only constrains, absent for the one which
 * acts. The dash survives greyscale, which is why the two kinds are not told apart by colour.
 */
const edgeKinds = {
    UNLOCKS: {label: "unlocks"},
    REQUIRES: {label: "required by", strokeDasharray: '6 4'},
}

/**
 * Builds the React Flow edges of a list of delivery map edges.
 *
 * The two kinds are drawn differently and not only labelled differently: `unlocks` acts, so it is a
 * solid line, while `requires` only constrains, so it is dashed. Colour is never the only carrier
 * here - the dash pattern survives greyscale, and each edge is labelled in words. That is also why
 * making the arrows more visible could not be done by colouring the two kinds differently.
 *
 * The label is read ALONG the arrow, and every edge runs from the prerequisite to whatever depends
 * on it. So the `REQUIRES` edge is labelled "required by": read from its source, `SILVER requires
 * GOLD` says the opposite of what the edge means, while `SILVER required by GOLD` says it. The edge
 * *kind* is still **Requires**, which is the word `CONTEXT.md` fixes for it - this is that word
 * rendered for the direction it is read in, not a second term.
 *
 * Reversing the arrow instead was the alternative and is worse: the direction is what elk lays the
 * map out along, so a reversed `requires` edge would put GOLD to the left of SILVER and the map
 * would stop reading left to right as a journey.
 *
 * The colour is NOT set here: it is theme-dependent, and applied by [withEdgeColor] at render time.
 * Everything this function decides comes from the edge's kind alone, which is what makes it a pure
 * mapping the layout can be computed from once.
 *
 * @param edges The map's edges, as the server sent them
 */
export function toFlowEdges(edges = []) {
    return edges.map(edge => {
        // An unrecognised kind is labelled with its own name and drawn solid: it says what the
        // server called it and claims nothing further.
        const {label = edge.kind, strokeDasharray} = edgeKinds[edge.kind] ?? {}
        return {
            id: edge.id,
            source: edge.source,
            target: edge.target,
            type: 'smoothstep',
            label,
            animated: false,
            style: {
                strokeWidth: EDGE_STROKE_WIDTH,
                strokeDasharray,
            },
            markerEnd: {
                type: MarkerType.ArrowClosed,
                width: EDGE_MARKER_PX / EDGE_STROKE_WIDTH,
                height: EDGE_MARKER_PX / EDGE_STROKE_WIDTH,
            },
            data: {kind: edge.kind},
        }
    })
}

/**
 * Paints laid-out edges in the theme's colour.
 *
 * Kept apart from [toFlowEdges] so that a change of theme repaints the map without relaying it out:
 * the layout runs in an effect keyed on the map, and adding the colour to that effect's inputs would
 * make switching to dark mode throw away every node the user had dragged.
 *
 * The line and its arrowhead take the SAME colour. An arrowhead in another shade reads as a separate
 * mark rather than as the end of that line - and colour is never what tells the two edge kinds apart,
 * which is the dash's job precisely because a dash survives greyscale.
 *
 * @param edges Edges from [toFlowEdges], laid out or not
 * @param color What to paint them. Required: React Flow's own default is the near-invisible grey
 * #1717 exists to fix, so falling back to it would be the bug rather than a safe default.
 */
export function withEdgeColor(edges, color) {
    return edges.map(edge => ({
        ...edge,
        style: {...edge.style, stroke: color},
        markerEnd: {...edge.markerEnd, color},
    }))
}

/**
 * Has the branch nothing to put on the map at all?
 *
 * A branch with no promotion level, typically. There is no picture to draw, only the explanation of
 * what one would hold.
 */
export function isMapEmpty(map) {
    return !map || (map.checkpoints ?? []).length === 0
}

/**
 * Are there checkpoints but nothing joining them?
 *
 * The map's subject is dependencies, so a branch with promotion levels and no auto promotion or
 * promotion dependency configuration anywhere has a map which says nothing - even though it is not
 * blank. The view still draws the promotion levels, and says alongside them what configuration would
 * join them up.
 */
export function hasNoDependencies(map) {
    return !isMapEmpty(map) && (map.edges ?? []).length === 0
}

/**
 * What the SHAPE of a map is, as one comparable value: which checkpoints it holds and which edges
 * join them, and nothing else.
 *
 * The delivery map refreshes itself, and almost nothing on it changes minute to minute - the
 * configuration is static, and only the build on each checkpoint moves. Re-running the elk layout on
 * every fetch would reshuffle the whole map under the reader's cursor once a minute, which is worse
 * than not refreshing at all. The layout therefore runs when THIS value changes, and the node
 * contents are updated in place the rest of the time - see [withCheckpointContents].
 *
 * Deliberately blind to three things:
 *
 * * the arrival of each checkpoint, which is precisely what moves between two fetches;
 * * the ORDER the checkpoints and edges come back in, which is not the shape of the map - the ids
 *   are sorted here so that a reordering alone never costs a relayout;
 * * the members of an aggregate, which are drawn inside their own node, on demand, and cost the
 *   layout nothing whatever they are.
 *
 * @param map The map as the server sent it, narrowed or not
 */
export function topologyKey(map) {
    if (!map) return '[[],[]]'
    const checkpoints = (map.checkpoints ?? []).map(it => it.id).sort()
    const edges = (map.edges ?? []).map(it => it.id).sort()
    return JSON.stringify([checkpoints, edges])
}

/**
 * Carries the checkpoints of a freshly fetched map onto the nodes already laid out.
 *
 * The other half of [topologyKey]: when the shape has not changed there is nothing to lay out again,
 * and what a refresh has to do is put the new build on each node WITHOUT touching its position. Node
 * ids are checkpoint ids (see [toFlowNodes]), which is what makes the two sides match up.
 *
 * A node whose checkpoint is not in the new map is left exactly as it is rather than dropped: its
 * absence means the topology changed, so a layout is already on its way, and removing it here would
 * blank it in the meantime.
 *
 * The SAME array comes back when nothing changed, because a new array is a new render of every node
 * in the graph.
 *
 * @param nodes The React Flow nodes, as the layout left them
 * @param checkpoints The checkpoints of the map just fetched
 */
export function withCheckpointContents(nodes = [], checkpoints = []) {
    const byId = new Map(checkpoints.map(it => [it.id, it]))
    let changed = false
    const refreshed = nodes.map(node => {
        const checkpoint = byId.get(node.id)
        if (!checkpoint || checkpoint === node.data?.checkpoint) return node
        changed = true
        return {...node, data: {...node.data, checkpoint}}
    })
    return changed ? refreshed : nodes
}

/**
 * The checkpoint kinds a validation stamp is drawn as.
 *
 * The aggregate belongs here as much as the stamp itself: it IS validation stamps, standing for the
 * ones an auto promotion pattern matches, so anything acting on stamps has to act on it too.
 */
const VALIDATION_STAMP = 'validation-stamp'
const VALIDATION_STAMP_PATTERN = 'validation-stamp-pattern'
const validationStampKinds = [VALIDATION_STAMP, VALIDATION_STAMP_PATTERN]

/**
 * Rebuilds a map around the checkpoints it keeps, dropping the edges left with a missing end.
 *
 * The one rule shared by everything which narrows a map - the stamp filter and the stamp toggle
 * alike - and the same rule the server applies when a contributor leaves a checkpoint out: a line to
 * nowhere reads as a broken map rather than as a hidden checkpoint.
 *
 * @param map The map being narrowed
 * @param kept The checkpoints which survive
 */
function keepCheckpoints(map, kept) {
    const ids = new Set(kept.map(it => it.id))
    return {
        ...map,
        checkpoints: kept,
        edges: (map.edges ?? []).filter(edge => ids.has(edge.source) && ids.has(edge.target)),
    }
}

/**
 * Shows or hides the validation stamps of a map.
 *
 * The reader's own switch, unlike [applyValidationStampFilter] which honours the branch's selected
 * filter. It is ON by default: a map opening on a chain of promotions with no visible cause would
 * hide the very thing which explains why any of them is granted. Turning it off is for reading the
 * promotion and slot shape of a branch whose stamps outnumber everything else on screen.
 *
 * @param map The map, narrowed by the filter or not
 * @param show Whether the validation stamps are drawn
 */
export function withValidationStamps(map, show = true) {
    if (!map || show) return map
    return keepCheckpoints(
        map,
        (map.checkpoints ?? []).filter(it => !validationStampKinds.includes(it.type)),
    )
}

/**
 * Narrows a map by the branch's selected validation stamp filter.
 *
 * The filter lives above the view switch precisely so that a user's filter follows them from one
 * content view to the next, so the map has to honour it like the others do. It touches validation
 * stamps only: a filter is a statement about stamps, and hiding a promotion level because of one
 * would be a different claim entirely.
 *
 * An aggregate checkpoint is narrowed through its members, and disappears when the filter leaves it
 * standing for nothing.
 *
 * Edges left with a missing end go with it - the same rule the server applies, for the same reason:
 * a line to nowhere reads as a broken map rather than as a hidden checkpoint.
 *
 * @param map The map as the server sent it
 * @param selectedFilter The active validation stamp filter, if any
 */
export function applyValidationStampFilter(map, selectedFilter) {
    const vsNames = selectedFilter?.vsNames
    if (!map || !vsNames) return map

    const kept = (map.checkpoints ?? []).flatMap(checkpoint => {
        if (checkpoint.type === VALIDATION_STAMP) {
            return vsNames.includes(checkpoint.name) ? [checkpoint] : []
        }
        if (checkpoint.type === VALIDATION_STAMP_PATTERN) {
            const members = (checkpoint.members ?? []).filter(it => vsNames.includes(it.name))
            return members.length > 0 ? [{...checkpoint, members}] : []
        }
        return [checkpoint]
    })

    return keepCheckpoints(map, kept)
}
