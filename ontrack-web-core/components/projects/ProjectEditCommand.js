import {Command} from "@components/common/Commands";
import {FaPencil} from "react-icons/fa6";
import {useContext} from "react";
import {EventsContext} from "@components/common/EventsContext";
import EditProjectDialog, {useEditProjectDialog} from "@components/projects/EditProjectDialog";

export default function ProjectEditCommand({project}) {

    const eventsContext = useContext(EventsContext)

    const onSuccess = () => {
        eventsContext.fireEvent("project.updated", {id: Number(project.id)})
    }

    const dialog = useEditProjectDialog({onSuccess})

    const editProject = () => {
        dialog.start({project})
    }

    return (
        <>
            <Command
                icon={<FaPencil/>}
                text="Edit project"
                action={editProject}
            />
            <EditProjectDialog dialog={dialog}/>
        </>
    )
}