import MainPage from "@components/layouts/MainPage";
import LoadingContainer from "@components/common/LoadingContainer";
import Dashboard from "@components/dashboards/Dashboard";

export default function DashboardPage({
                                          title,
                                      }) {

    const commands = [
        // TODO DashboardCommandMenu
    ]

    return (
        <>
            <MainPage
                title={title}
                breadcrumbs={[]}
                commands={commands}
            >
                <Dashboard/>
            </MainPage>
        </>
    )
}
