import {useEffect, useState} from "react";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";
import Head from "next/head";
import {projectTitle} from "@components/common/Titles";
import {projectBreadcrumbs} from "@components/common/Breadcrumbs";
import {homeUri} from "@components/common/Links";
import DashboardPage from "@components/dashboards/DashboardPage";

export default function ProjectView({id}) {

    const [loadingProject, setLoadingProject] = useState(true)
    const [project, setProject] = useState({})

    useEffect(() => {
        if (id) {
            setLoadingProject(true)
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
            }).finally(() => {
                setLoadingProject(false)
            })
        }
    }, [id])

    return (
        <>
            <Head>
                {projectTitle(project)}
            </Head>
            <DashboardPage
                title={project.name}
                breadcrumbs={projectBreadcrumbs(project)}
                closeHref={homeUri()}
                loading={loadingProject}
                context="project"
                contextId={project.id}
            />
        </>
    )
}