import MainPage from "@components/layouts/MainPage";
import {CloseCommand, DashboardEditCommand} from "@components/common/Commands";
import LoadingContainer from "@components/common/LoadingContainer";
import Dashboard from "@components/dashboards/Dashboard";

export default function DashboardPage({
                                          title, breadcrumbs, closeHref,
                                          loading,
                                          context, contextId
                                      }) {

    const commands = [
        <DashboardEditCommand/>,
        <CloseCommand key="close" href={closeHref}/>,
    ]

    return (
        <>
            <MainPage
                title={title}
                breadcrumbs={breadcrumbs}
                commands={commands}
            >
                <LoadingContainer loading={loading}>
                    <Dashboard context={context} contextId={contextId}/>
                </LoadingContainer>
            </MainPage>
        </>
    )
}