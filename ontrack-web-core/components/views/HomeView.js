import MainPage from "@components/layouts/MainPage";
import ProjectList, {useProjectList} from "@components/projects/ProjectList";
import {Command} from "@components/common/Commands";
import {PlusOutlined} from "@ant-design/icons";
import NewProjectDialog, {useNewProjectDialog} from "@components/projects/NewProjectDialog";

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
            icon={<PlusOutlined/>}
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