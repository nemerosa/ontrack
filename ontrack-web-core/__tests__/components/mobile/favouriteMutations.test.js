import {favouriteMutation, FAVOURITE_ENTITY_TYPES} from "@components/mobile/favourites/favouriteMutations"

describe('favouriteMutations', () => {

    it('covers the two entity types a user can favourite', () => {
        expect(FAVOURITE_ENTITY_TYPES).toEqual(['project', 'branch'])
    })

    it.each([
        ['project', true, 'favouriteProject'],
        ['project', false, 'unfavouriteProject'],
        ['branch', true, 'favouriteBranch'],
        ['branch', false, 'unfavouriteBranch'],
    ])('%s reaching %s calls %s', (type, favourite, mutation) => {
        const {query, userNode} = favouriteMutation(type, favourite)
        expect(userNode).toEqual(mutation)
        // The state to *reach*, not the one to leave: getting that backwards
        // silently makes every toggle a no-op that looks like it worked.
        expect(query).toContain(`${mutation}(input: {id: $id})`)
    })

    it('refuses an entity type it has no mutations for', () => {
        // A typo here would otherwise reach the server as an undefined query.
        expect(() => favouriteMutation('build', true)).toThrow(/build/)
    })
})
