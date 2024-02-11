import {generate} from "@ontrack/utils";
import {graphQLCallMutation} from "@ontrack/graphql";
import {gql} from "graphql-request";
import {JIRAConfigurations} from "@ontrack/extensions/jira/jira";

export class OntrackConfigurations {

    constructor(ontrack) {
        this.ontrack = ontrack
        this.jira = new JIRAConfigurations(ontrack)
    }

}
