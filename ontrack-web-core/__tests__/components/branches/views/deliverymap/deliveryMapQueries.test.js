import fs from "fs";
import path from "path";
import {buildSchema, parse, validate} from "graphql";

import {gqlDeliveryMap} from "@components/branches/views/deliverymap/deliveryMapQueries";

/**
 * The delivery map's document, checked against the schema.
 *
 * Worth checking here rather than in the browser because the failure is silent in the worst way: a
 * mistyped field makes the WHOLE query fail, so the view falls back to its empty state and says the
 * branch has nothing on its map - which is a plausible thing for a branch, and therefore reads as
 * data rather than as a defect.
 */
describe('delivery map queries', () => {

    const schema = buildSchema(
        fs.readFileSync(path.join(process.cwd(), 'ontrack.graphql'), 'utf-8'),
    )

    it('the delivery map query is valid', () => {
        const errors = validate(schema, parse(gqlDeliveryMap))
        expect(errors.map(error => error.message)).toEqual([])
    })

    it('asks for the members of an aggregate checkpoint', () => {
        // An aggregate stands for its members; without them it can say nothing at all
        expect(gqlDeliveryMap).toContain('members')
    })

    it('reads the checkpoint payload as opaque JSON', () => {
        // Checkpoint kinds are open, so a typed field per kind would mean this query changing every
        // time an extension contributes one
        expect(gqlDeliveryMap).toMatch(/^\s+data$/m)
    })

})
