import MainPage from "@components/layouts/MainPage";
import {CloseCommand, DashboardEditCommand} from "@components/common/Commands";
import LoadingContainer from "@components/common/LoadingContainer";
import Dashboard from "@components/dashboards/Dashboard";
import {useState} from "react";

export default function DashboardPage({
                                          title, breadcrumbs, closeHref,
                                          loading,
                                          context, contextId
                                      }) {

    const [editionMode, setEditionMode] = useState(false)

    const commands = [
        <DashboardEditCommand editionMode={editionMode} onClick={() => setEditionMode(true)}/>,
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
                    <Dashboard
                        context={context}
                        contextId={contextId}
                        editionMode={editionMode}
                    />
                </LoadingContainer>
            </MainPage>
        </>
    )
}