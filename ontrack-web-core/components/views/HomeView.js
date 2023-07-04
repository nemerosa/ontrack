import MainPage from "@components/layouts/MainPage";
import Dashboard from "@components/dashboards/Dashboard";
import {useProjectList} from "@components/projects/ProjectList";
import NewProjectDialog, {useNewProjectDialog} from "@components/projects/NewProjectDialog";
import {Command, DashboardEditCommand} from "@components/common/Commands";
import {FaPlus} from "react-icons/fa";
import {useContext, useEffect, useState} from "react";
import {UserContext} from "@components/providers/UserProvider";

export default function HomeView() {

    const user = useContext(UserContext)

    const projectList = useProjectList()

    const newProjectDialog = useNewProjectDialog({
        onSuccess: () => {
            projectList.refresh()
        }
    })
    const newProject = () => {
        newProjectDialog.start()
    }

    const [commands, setCommands] = useState([])

    useEffect(() => {
        if (user) {
            const commands = []
            if (user.authorizations?.project?.create) {
                commands.push(
                    <Command
                        key="new-project"
                        icon={<FaPlus/>}
                        text="New project"
                        action={newProject}
                    />,
                )
            }
            commands.push(
                <DashboardEditCommand key="dashboard-edit"/>,
            )
            setCommands(commands)
        }
    }, [user])

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