/**
 * The four favourite mutations, as a lookup rather than four call sites.
 *
 * Favouriting a project and favouriting a branch differ in nothing but the
 * mutation's name, so the mobile UI has one toggle and this table behind it.
 * Kept as a pure module - no React, no client - so the mapping can be tested
 * without rendering anything.
 *
 * These are the existing server mutations; the mobile UI adds no GraphQL of its
 * own. See `doc/dev-guide/ui/mobile-ui.md`.
 */

import {gql} from "graphql-request"

/**
 * A `favourite`/`unfavourite` pair, keyed by the state the mutation *reaches*.
 * `true` marks, `false` unmarks.
 */
const MUTATIONS = {
    project: {
        true: 'favouriteProject',
        false: 'unfavouriteProject',
    },
    branch: {
        true: 'favouriteBranch',
        false: 'unfavouriteBranch',
    },
}

/** The entity types a mobile user can favourite. */
export const FAVOURITE_ENTITY_TYPES = Object.keys(MUTATIONS)

/**
 * The mutation taking an entity to a favourite state.
 *
 * @param {string} type One of {@link FAVOURITE_ENTITY_TYPES}.
 * @param {boolean} favourite The state to reach - `true` to mark as a favourite.
 * @returns {{query: string, userNode: string}} The GraphQL document, and the
 *   name of the payload node `useMutation` reads the errors off.
 */
export function favouriteMutation(type, favourite) {
    const userNode = MUTATIONS[type]?.[String(Boolean(favourite))]
    if (!userNode) {
        throw new Error(`No favourite mutation for entity type "${type}".`)
    }
    return {
        userNode,
        // All four have the same shape - a single `id` - so one template covers
        // them. `errors` because every Yontrack mutation answers with a payload
        // carrying user errors rather than failing the request.
        query: gql`
            mutation ${userNode[0].toUpperCase()}${userNode.slice(1)}($id: Int!) {
                ${userNode}(input: {id: $id}) {
                    errors {
                        message
                    }
                }
            }
        `,
    }
}
