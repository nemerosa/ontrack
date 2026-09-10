import {FaStamp} from "react-icons/fa";
import {withValidationStamps} from "@components/branches/views/deliverymap/deliveryMapModel";

/**
 * What the reader can take off the delivery map, and how each of those is taken off.
 *
 * A LIST rather than one boolean, because this is not going to stay at one entry: the notification
 * and workflow checkpoints of #1711 and the previous-promotion edges of #1710 are both things a
 * reader will want out of the way while reading the rest. Adding one is an entry here - a label, an
 * icon, and how it narrows the map - and nothing else changes: the toolbar draws itself from this
 * list, and the view folds the list over the map.
 *
 * That is also why these controls are LABELLED and live in the view's toolbar rather than as icons in
 * the graph's control bar: three unlabelled eyes stacked in a corner say nothing about what each of
 * them hides. The control bar keeps what acts on the drawing itself - zooming, fitting, the layout.
 *
 * Everything is shown by default. Each entry carries:
 *
 * * `id` — stable key, used in the toolbar and in the stored preference
 * * `label` / `icon` — how the toggle names what it draws
 * * `title` — what the toggle says on hover
 * * `apply` — narrows the map when the entry is turned off; given the map and whether it is shown
 */
export const visibilityKinds = [
    {
        id: 'validation-stamps',
        label: "Validation stamps",
        icon: <FaStamp/>,
        title: "The validation stamps which grant the promotions, and the patterns standing for them",
        apply: (map, shown) => withValidationStamps(map, shown),
    },
]

/**
 * Is a kind drawn, according to the reader's stored preferences?
 *
 * Anything not named in [visibility] is SHOWN. The preference records what has been turned off, so a
 * kind added to the list above later is drawn for everyone rather than hidden from the readers who
 * happen to have a preference stored.
 *
 * @param visibility The stored preferences, keyed by kind id
 * @param id Id of the kind
 */
export function isShown(visibility, id) {
    return visibility?.[id] !== false
}

/**
 * Narrows a map by everything the reader has turned off.
 *
 * @param map The map, narrowed by the branch's validation stamp filter or not
 * @param visibility The stored preferences, keyed by kind id
 */
export function applyVisibility(map, visibility) {
    return visibilityKinds.reduce(
        (narrowed, kind) => kind.apply(narrowed, isShown(visibility, kind.id)),
        map,
    )
}
