import {generate} from "@ontrack/utils";
import {graphQLCallMutation} from "@ontrack/graphql";
import {gql} from "graphql-request";
import {createBranch} from "@ontrack/branch";


export const createProject = async (ontrack, name) => {
    const actualName = name ?? generate('prj_')

    const data = await graphQLCallMutation(
        ontrack.connection,
        'createProject',
        gql`
            mutation CreateProject(
                $name: String!,
            ) {
                createProject(input: {
                    name: $name,
                }) {
                    project {
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
            name: actualName,
        }
    )

    return projectInstance(ontrack, data.createProject.project)
}

const projectInstance = (ontrack, data) => {
    const project = {
        ontrack,
        ...data,
    }

    project.createBranch = async (name) => createBranch(project, name)

    return project
}