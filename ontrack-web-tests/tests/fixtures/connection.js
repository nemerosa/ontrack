import {test as base} from '@playwright/test'
import {createConnection} from "@ontrack/connection";
import {Ontrack} from "@ontrack/ontrack";

export const test = base.extend({
    ontrack: async ({}, use) => {
        const connection = await createConnection()
        const ontrack = new Ontrack(connection)
        await use(ontrack)
    }
})
