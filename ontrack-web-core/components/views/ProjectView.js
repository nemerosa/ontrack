import MainPage from "@components/layouts/MainPage";
import {useEffect, useState} from "react";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";
import Head from "next/head";
import {projectTitle} from "@components/common/Titles";
import {projectBreadcrumbs} from "@components/common/Breadcrumbs";
import {CloseCommand, DashboardEditCommand} from "@components/common/Commands";
import {homeUri} from "@components/common/Links";
import LoadingContainer from "@components/common/LoadingContainer";
import Dashboard from "@components/dashboards/Dashboard";

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
                setLoadingProject(false)
            })
        }
    }, [id])

    const commands = [
        <CloseCommand key="close" href={homeUri()}/>,
        <DashboardEditCommand key="dashboard-edit"/>,
    ]

    return (
        <>
            <Head>
                {projectTitle(project)}
            </Head>
            <MainPage
                title={project.name}
                breadcrumbs={projectBreadcrumbs(project)}
                commands={commands}
            >
                <LoadingContainer loading={loadingProject} tip="Loading project">
                    <Dashboard context="project" contextId={project.id}/>
                </LoadingContainer>
            </MainPage>
        </>
    )
}