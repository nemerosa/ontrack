import MainPage from "@components/layouts/MainPage";
import {CloseCommand} from "@components/common/Commands";
import LoadingContainer from "@components/common/LoadingContainer";
import Dashboard from "@components/dashboards/Dashboard";
import DashboardContextProvider, {DashboardContext} from "@components/dashboards/DashboardContext";

export default function DashboardPage({
                                          title, breadcrumbs, closeHref,
                                          loading,
                                          context, contextId
                                      }) {

    // const [editionMode, setEditionMode] = useState(false)

    const commands = [
        // <DashboardEditCommand editionMode={editionMode} onClick={() => setEditionMode(true)}/>,
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
                    <DashboardContextProvider context={context} contextId={contextId}>
                        <Dashboard/>
                    </DashboardContextProvider>
                </LoadingContainer>
            </MainPage>
        </>
    )
}