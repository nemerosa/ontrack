import {generate} from "@ontrack/utils";
import {graphQLCall, graphQLCallMutation} from "@ontrack/graphql";
import {gql} from "graphql-request";
import {createBranch} from "@ontrack/branch";
import {registerNotificationExtensions} from "@ontrack/extensions/notifications/notifications";

const gqlProjectData = gql`
    fragment ProjectData on Project {
        id
        name
        disabled
    }
`

export const projectList = async (ontrack) => {
    const data = await graphQLCall(
        ontrack.connection,
        gql`
            query ProjectList {
                projects {
                    ...ProjectData
                }
            }
            ${gqlProjectData}
        `
    )
    return data.projects.map(it => projectInstance(ontrack, it))
}

export const getProjectById = async (ontrack, id) => {
    const data = await graphQLCall(
        ontrack.connection,
        gql`
            query GetProjectById($id: Int!) {
                project(id: $id) {
                    ...ProjectData
                }
            }
            ${gqlProjectData}
        `,
        {id: Number(id)}
    )
    return projectInstance(ontrack, data.project)
}

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
                        ...ProjectData
                    }
                    errors {
                        message
                    }
                }
            }
            ${gqlProjectData}
        `,
        {
            name: actualName,
        }
    )

    return projectInstance(ontrack, data.createProject.project)
}

export const projectInstance = (ontrack, data) => {
    const project = {
        ontrack,
        type: 'PROJECT',
        ...data,
    }

    project.createBranch = async (name) => createBranch(project, name)

    // Notifications methods
    registerNotificationExtensions(project)

    return project
}