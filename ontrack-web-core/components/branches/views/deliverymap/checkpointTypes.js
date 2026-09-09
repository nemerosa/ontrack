import PromotionLevelCheckpoint
    from "@components/branches/views/deliverymap/checkpoints/PromotionLevelCheckpoint";
import ValidationStampCheckpoint
    from "@components/branches/views/deliverymap/checkpoints/ValidationStampCheckpoint";
import ValidationStampPatternCheckpoint
    from "@components/branches/views/deliverymap/checkpoints/ValidationStampPatternCheckpoint";
import UnknownCheckpoint from "@components/branches/views/deliverymap/checkpoints/UnknownCheckpoint";

/**
 * Registry of the checkpoint kinds this frontend can draw.
 *
 * The set of kinds is OPEN on the server: an extension contributes a kind the core has never heard
 * of, as the environments extension will for slots in #1704. A registry keyed by the kind's own
 * string is what lets a new kind be added by adding a component and a line here.
 *
 * Each entry carries:
 *
 * * `component` — receives the checkpoint, draws the inside of its node
 * * `width` / `height` — what the layout reserves for a node of this kind, in pixels
 *
 * Sizes belong to the kind rather than to the node because elk needs them BEFORE anything is
 * rendered: an aggregate is taller than a promotion level whether or not it is expanded, and a
 * layout computed against one size and drawn at another overlaps.
 */
export const checkpointTypes = {
    'promotion-level': {
        component: PromotionLevelCheckpoint,
        width: 240,
        height: 90,
    },
    'validation-stamp': {
        component: ValidationStampCheckpoint,
        width: 260,
        height: 90,
    },
    'validation-stamp-pattern': {
        component: ValidationStampPatternCheckpoint,
        width: 260,
        height: 110,
    },
}

/**
 * What to draw a checkpoint of the given kind with.
 *
 * An unknown kind falls back rather than failing: the frontend may be older than the backend it
 * talks to, and one unrecognised kind must not take the map with it.
 *
 * @param type Kind of the checkpoint
 */
export function getCheckpointType(type) {
    return checkpointTypes[type] ?? {
        component: UnknownCheckpoint,
        width: 240,
        height: 90,
    }
}
