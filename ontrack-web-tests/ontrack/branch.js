import {generate} from "@ontrack/utils";
import {graphQLCallMutation} from "@ontrack/graphql";
import {gql} from "graphql-request";
import {createValidationStamp} from "@ontrack/validationStamp";
import {createBuild} from "@ontrack/build";

export const createBranch = async (project, name) => {
    const actualName = name ?? generate('bch_')

    const data = await graphQLCallMutation(
        project.ontrack.connection,
        'createBranch',
        gql`
            mutation CreateBranch(
                $project: String!,
                $name: String!,
            ) {
                createBranch(input: {
                    projectName: $project,
                    name: $name,
                }) {
                    branch {
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
            project: project.name,
            name: actualName,
        }
    )

    return branchInstance(project.ontrack, data.createBranch.branch, project)
}

const branchInstance = (ontrack, data, project) => {
    const branch = {
        ontrack,
        ...data,
        project,
    }

    branch.createValidationStamp = async (name) => createValidationStamp(branch, name)
    branch.createBuild = async (name) => createBuild(branch, name)

    return branch
}