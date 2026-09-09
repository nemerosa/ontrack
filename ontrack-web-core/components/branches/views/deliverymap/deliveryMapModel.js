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
 * Builds the React Flow edges of a list of delivery map edges.
 *
 * The two kinds are drawn differently and not only labelled differently: `unlocks` acts, so it is a
 * solid line, while `requires` only constrains, so it is dashed. Colour is never the only carrier
 * here either - the dash pattern survives greyscale, and each edge is labelled in words.
 */
export function toFlowEdges(edges = []) {
    return edges.map(edge => ({
        id: edge.id,
        source: edge.source,
        target: edge.target,
        type: 'smoothstep',
        label: edge.kind === 'REQUIRES' ? "requires" : "unlocks",
        animated: false,
        style: edge.kind === 'REQUIRES' ? {strokeDasharray: '6 4'} : undefined,
        markerEnd: {
            type: MarkerType.ArrowClosed,
            width: 16,
            height: 16,
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
