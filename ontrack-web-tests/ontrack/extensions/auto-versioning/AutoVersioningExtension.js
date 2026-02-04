import {graphQLCallMutation} from "@ontrack/graphql";
import {gql} from "graphql-request";
import {AutoVersioningAuditMgt} from "@ontrack/extensions/auto-versioning/AutoVersioningAuditMgt";

export class AutoVersioningExtension {

    constructor(ontrack) {
        this.ontrack = ontrack
        this.audit = new AutoVersioningAuditMgt(ontrack)
    }

    async autoVersioningCheck(build) {
        return await graphQLCallMutation(
            this.ontrack.connection,
            'checkAutoVersioning',
            gql`
                mutation AutoVersioningCheck($project: String!, $branch: String!, $build: String!) {
                    checkAutoVersioning(input: {
                        project: $project,
                        branch: $branch,
                        build: $build
                    }) {
                        errors {
                            message
                        }
                    }
                }
            `,
            {
                project: build.branch.project.name,
                branch: build.branch.name,
                build: build.name,
            }
        )
    }

    async setAutoVersioningConfig(branch, config) {
        await graphQLCallMutation(
            this.ontrack.connection,
            'setAutoVersioningConfig',
            gql`
                mutation SetAutoVersioningConfig(
                    $branchId: Int!,
                    $configuration: AutoVersioningSourceConfigInput!,
                ) {
                    setAutoVersioningConfig(input: {
                        branchId: $branchId,
                        configurations: [$configuration],
                    }) {
                        errors {
                            message
                        }
                    }
                }
            `,
            {
                branchId: Number(branch.id),
                configuration: config,
            }
        )
    }

}