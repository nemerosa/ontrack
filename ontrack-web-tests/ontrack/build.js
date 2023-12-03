import {generate} from "@ontrack/utils";
import {graphQLCallMutation} from "@ontrack/graphql";
import {gql} from "graphql-request";
import {createValidationRun} from "@ontrack/validationRun";

export const createBuild = async (branch, name) => {
    const actualName = name ?? generate('b_')

    const data = await graphQLCallMutation(
        branch.ontrack.connection,
        'createBuild',
        gql`
            mutation CreateBuild(
                $branchId: Int!,
                $name: String!,
            ) {
                createBuild(input: {
                    branchId: $branchId,
                    name: $name,
                }) {
                    build {
                        id
                        name
                    }
                    errors {
                        message
                    }
                }
            }
        `,
        {
            branchId: branch.id,
            name: actualName,
        }
    )

    return buildInstance(branch, data.createBuild.build)
}


const buildInstance = (branch, data) => {
    const build = {
        ontrack: branch.ontrack,
        ...data,
        branch,
    }

    build.validate = async (validationStamp, config) => createValidationRun(build, validationStamp, config)

    return build
}