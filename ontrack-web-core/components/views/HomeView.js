import MainPage from "@components/layouts/MainPage";
import Dashboard from "@components/dashboards/Dashboard";
import {useProjectList} from "@components/projects/ProjectList";
import NewProjectDialog, {useNewProjectDialog} from "@components/projects/NewProjectDialog";
import {Command, DashboardEditCommand} from "@components/common/Commands";
import {FaPlus} from "react-icons/fa";

export default function HomeView() {

    const projectList = useProjectList()

    const newProjectDialog = useNewProjectDialog({
        onSuccess: () => {
            projectList.refresh()
        }
    })
    const newProject = () => {
        newProjectDialog.start()
    }

    const commands = [
        <Command
            key="new-project"
            icon={<FaPlus/>}
            text="New project"
            action={newProject}
        />,
        <DashboardEditCommand key="dashboard-edit"/>,
    ]

    return (
        <>
            <MainPage
                title="Home"
                commands={commands}
            >
                <Dashboard context="home"/>
            </MainPage>
            <NewProjectDialog
                newProjectDialog={newProjectDialog}
            />
        </>
    )
}