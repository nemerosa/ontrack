import PromotionLevelCheckpoint
    from "@components/branches/views/deliverymap/checkpoints/PromotionLevelCheckpoint";
import ValidationStampCheckpoint
    from "@components/branches/views/deliverymap/checkpoints/ValidationStampCheckpoint";
import ValidationStampPatternCheckpoint
    from "@components/branches/views/deliverymap/checkpoints/ValidationStampPatternCheckpoint";
import UnresolvedCheckpoint from "@components/branches/views/deliverymap/checkpoints/UnresolvedCheckpoint";
import UnknownCheckpoint from "@components/branches/views/deliverymap/checkpoints/UnknownCheckpoint";
import SlotCheckpoint from "@components/extension/environments/deliverymap/SlotCheckpoint";

/**
 * Registry of the checkpoint kinds this frontend can draw.
 *
 * The set of kinds is OPEN on the server: an extension contributes a kind the core has never heard
 * of, as the environments extension does for `slot`. A registry keyed by the kind's own string is
 * what lets a new kind be added by adding a component and a line here.
 *
 * The entry for a kind an extension contributes names that extension's component, which is a core to
 * extension import the decoration seam would avoid with a dynamic one. It is accepted here because
 * the sizes below cannot be: elk needs them before anything is rendered, so they belong to the
 * registry whatever else does, and a registry which named the kind for its size while loading its
 * component by path would name it twice for one gain.
 *
 * Each entry carries:
 *
 * * `component` — receives the checkpoint, draws the inside of its node
 * * `width` / `height` — what the layout reserves for a node of this kind, in pixels
 *
 * Sizes belong to the kind rather than to the node because elk needs them BEFORE anything is
 * rendered, and a layout computed against one size and drawn at another overlaps. That is also why
 * no checkpoint may grow after it is laid out: the aggregate shows its members in a popover, drawn
 * outside the flow, rather than by growing to fit them.
 *
 * Every kind which can name a build reserves room for the LAG MARKER beside it - "at head" or
 * "5 behind" - which is why the widths here are not the widths of a name and a build alone.
 */
export const checkpointTypes = {
    'promotion-level': {
        component: PromotionLevelCheckpoint,
        width: 310,
        height: 90,
    },
    'validation-stamp': {
        component: ValidationStampCheckpoint,
        width: 330,
        height: 90,
    },
    // No wider than before the lag marker: the aggregate names no build of its own. Its members do,
    // and they are drawn in a popover outside the flow, which the layout does not pay for.
    'validation-stamp-pattern': {
        component: ValidationStampPatternCheckpoint,
        width: 260,
        height: 110,
    },
    // Wider and taller than a promotion level. The width is set by the longest thing a slot ever
    // says - "Unreachable from this branch" - and the height by the third line it may draw, saying
    // either that its build comes from another branch or that this branch can never reach it.
    // Measured against the widest content on purpose: a node narrower than its reservation only
    // leaves a gap, while one wider than it covers whatever elk placed beside it.
    'slot': {
        component: SlotCheckpoint,
        width: 430,
        height: 110,
    },
    // A name a configuration asked for and which matches nothing. Two lines only - the name and what
    // failed to match - so it is shorter than a slot, and no build line can ever appear on it.
    'unresolved': {
        component: UnresolvedCheckpoint,
        width: 280,
        height: 90,
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
        width: 310,
        height: 90,
    }
}
