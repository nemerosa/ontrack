import MainPage from "@components/layouts/MainPage";
import ProjectList, {useProjectList} from "@components/projects/ProjectList";
import {Command} from "@components/common/Commands";
import NewProjectDialog, {useNewProjectDialog} from "@components/projects/NewProjectDialog";
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
    ]

    return (
        <>
            <MainPage
                title="Home"
                commands={commands}
            >
                <ProjectList
                    projectList={projectList}
                />
            </MainPage>
            <NewProjectDialog
                newProjectDialog={newProjectDialog}
            />
        </>
    )
}