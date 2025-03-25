import Head from "next/head";
import MainPage from "@components/layouts/MainPage";
import {CloseCommand} from "@components/common/Commands";
import {projectUri} from "@components/common/Links";
import LoadingContainer from "@components/common/LoadingContainer";
import EnvironmentsWarning from "@components/extension/environments/EnvironmentsWarning";
import {useProject} from "@components/services/useProject";
import ProjectEnvironmentsContextProvider
    from "@components/extension/environments/project/ProjectEnvironmentsContextProvider";
import {projectTitle} from "@components/common/Titles";
import {downToProjectBreadcrumbs} from "@components/common/Breadcrumbs";
import ProjectEnvironmentsMgt from "@components/extension/environments/project/ProjectEnvironmentsMgt";
import EnvironmentsCommand from "@components/extension/environments/EnvironmentsCommand";

export default function ProjectEnvironmentsView({id}) {
    const {project, loading} = useProject({id})
    return (
        <>
            <Head>
                {project && projectTitle(project, "Environments")}
            </Head>
            <MainPage
                title="Environments"
                warning={<EnvironmentsWarning/>}
                breadcrumbs={project ? downToProjectBreadcrumbs({project}) : []}
                commands={[
                    <EnvironmentsCommand key="environments" text="All environments"/>,
                    <CloseCommand key="close" href={projectUri({id})}/>,
                ]}
            >
                <LoadingContainer loading={loading}>
                    <ProjectEnvironmentsContextProvider id={id} qualifier={""}>
                        <ProjectEnvironmentsMgt/>
                    </ProjectEnvironmentsContextProvider>
                </LoadingContainer>
            </MainPage>
        </>
    )
}