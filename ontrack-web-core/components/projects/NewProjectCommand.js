import NewProjectDialog, {useNewProjectDialog} from "@components/projects/NewProjectDialog";
import {useContext} from "react";
import {EventsContext} from "@components/common/EventsContext";
import {Command} from "@components/common/Commands";
import {FaPlus} from "react-icons/fa";

export default function NewProjectCommand() {

    const eventsContext = useContext(EventsContext)

    const newProjectDialog = useNewProjectDialog({
        onSuccess: () => {
            eventsContext.fireEvent("project.created")
        }
    })

    const onCreateProject = () => {
        newProjectDialog.start()
    }

    return (
        <>
            <NewProjectDialog newProjectDialog={newProjectDialog}/>
            <Command
                icon={<FaPlus/>}
                text="New project"
                title="Creates a new project"
                action={onCreateProject}
            />
        </>
    )
}