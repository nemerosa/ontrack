import {restCallPost} from "@ontrack/rest";

export class SearchMgt {

    constructor(ontrack) {
        this.ontrack = ontrack
    }

    async forceIndexation({type}) {
        const path = `/rest/search/index/type/${type}`
        await restCallPost(
            this.ontrack.connection,
            path,
            {}
        )
    }

}