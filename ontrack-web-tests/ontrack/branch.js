import {generate} from "@ontrack/utils";
import {graphQLCall, graphQLCallMutation} from "@ontrack/graphql";
import {gql} from "graphql-request";
import {createValidationStamp} from "@ontrack/validationStamp";
import {createBuild} from "@ontrack/build";
import {createPromotionLevel} from "@ontrack/promotionLevel";
import {projectInstance} from "@ontrack/project";

const gqlBranchData = gql`
    fragment BranchData on Branch {
        id
        name
        disabled
        project {
            id
            name
        }
    }
`

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

export const getBranchById = async (ontrack, id) => {
    const data = await graphQLCall(
        ontrack.connection,
        gql`
            query GetBranchById($id: Int!) {
                branch(id: $id) {
                    ...BranchData
                }
            }
            ${gqlBranchData}
        `,
        {id: Number(id)}
    )
    const project = projectInstance(ontrack, data.branch.project)
    return branchInstance(ontrack, data.branch, project)
}

const branchInstance = (ontrack, data, project) => {
    const branch = {
        ontrack,
        ...data,
        project,
    }

    branch.createPromotionLevel = async (name) => createPromotionLevel(branch, name)
    branch.createValidationStamp = async (name) => createValidationStamp(branch, name)
    branch.createBuild = async (name) => createBuild(branch, name)

    return branch
}