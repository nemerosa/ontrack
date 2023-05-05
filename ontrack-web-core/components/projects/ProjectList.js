import {useEffect, useState} from "react";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";
import ProjectBox from "@components/projects/ProjectBox";
import {Space} from "antd";
import LoadingContainer from "@components/common/LoadingContainer";

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

    const [loadingProjects, setLoadingProjects] = useState(true)
    const [projects, setProjects] = useState([])

    useEffect(() => {
        setLoadingProjects(true)
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
            setLoadingProjects(false)
        })
    }, [projectList.projectsReload])

    return (
        <>
            <LoadingContainer loading={loadingProjects} tip="Loading projects">
                <Space direction="horizontal" size={16} wrap>
                    {projects.map(project => <ProjectBox key={project.id} project={project}/>)}
                </Space>
            </LoadingContainer>
        </>
    )
}