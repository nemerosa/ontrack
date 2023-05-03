import {useEffect, useState} from "react";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";
import ProjectBox from "@components/projects/ProjectBox";
import {Space} from "antd";

export default function ProjectList() {

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
    }, [])

    return (
        <>
            <Space direction="horizontal" size={16} wrap>
                { projects.map(project => <ProjectBox project={project}/> ) }
            </Space>
        </>
    )
}