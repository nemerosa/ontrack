import MainPage from "@components/layouts/MainPage";
import {useEffect, useState} from "react";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";
import Head from "next/head";
import {projectTitle} from "@components/common/Titles";
import {projectBreadcrumbs} from "@components/common/Breadcrumbs";

export default function ProjectView({id}) {

    const [project, setProject] = useState({})

    useEffect(() => {
        if (id) {
            graphQLCall(
                gql`
                    query GetProject($id: Int!) {
                        projects(id: $id) {
                            id
                            name
                        }
                    }
                `,
                {id}
            ).then(data => {
                setProject(data.projects[0])
            })
        }
    }, [id])

    return (
        <>
            <Head>
                {projectTitle(project)}
            </Head>
            <MainPage
                title={project.name}
                breadcrumbs={projectBreadcrumbs(project)}
            >
                {project.name}
            </MainPage>
        </>
    )
}