import {useEffect, useState} from "react";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";
import ProjectBox from "@components/projects/ProjectBox";
import {Space} from "antd";

export function useProjectList() {
    const [projectsReload, setProjectReload] = useState(0)
    return {
        projectsReload: projectsReload,
        refresh: () => {
            setProjectReload(projectsReload + 1)
        },
    }
}

export default function ProjectList({projectList}) {

    const [projects, setProjects] = useState([])

    useEffect(() => {
        graphQLCall(
            gql`
                query GetProjects {
                    projects {
                        id
                        name
                        favourite
                    }
                }
            `
        ).then(data => {
            setProjects(data.projects)
        })
    }, [projectList.projectsReload])

    return (
        <>
            <Space direction="horizontal" size={16} wrap>
                { projects.map(project => <ProjectBox key={project.id} project={project}/> ) }
            </Space>
        </>
    )
}