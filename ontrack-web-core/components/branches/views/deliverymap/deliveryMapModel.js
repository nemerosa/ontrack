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
 */
const EDGE_STROKE_WIDTH = 1.5
const EDGE_MARKER_SIZE = 22

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
 * @param edges The map's edges, as the server sent them
 * @param color What to draw the lines and their arrowheads in. It has to come from the caller: the
 * map is drawn in both light and dark mode, and this is a pure function with no theme to read.
 * Omitted, React Flow's own default colour applies, which is why nothing here breaks without it.
 */
export function toFlowEdges(edges = [], {color} = {}) {
    return edges.map(edge => ({
        id: edge.id,
        source: edge.source,
        target: edge.target,
        type: 'smoothstep',
        label: edge.kind === 'REQUIRES' ? "required by" : "unlocks",
        animated: false,
        style: {
            stroke: color,
            strokeWidth: EDGE_STROKE_WIDTH,
            // Dashed for `requires` only: it constrains where `unlocks` acts
            strokeDasharray: edge.kind === 'REQUIRES' ? '6 4' : undefined,
        },
        markerEnd: {
            type: MarkerType.ArrowClosed,
            width: EDGE_MARKER_SIZE,
            height: EDGE_MARKER_SIZE,
            // The same colour as the line it ends: an arrowhead in a different shade reads as a
            // separate mark rather than as the end of that line.
            color,
        },
        data: {kind: edge.kind},
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
        if (checkpoint.type === 'validation-stamp') {
            return vsNames.includes(checkpoint.name) ? [checkpoint] : []
        }
        if (checkpoint.type === 'validation-stamp-pattern') {
            const members = (checkpoint.members ?? []).filter(it => vsNames.includes(it.name))
            return members.length > 0 ? [{...checkpoint, members}] : []
        }
        return [checkpoint]
    })

    const ids = new Set(kept.map(it => it.id))
    return {
        ...map,
        checkpoints: kept,
        edges: (map.edges ?? []).filter(edge => ids.has(edge.source) && ids.has(edge.target)),
    }
}
