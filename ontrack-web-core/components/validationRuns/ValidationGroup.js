import {Space} from "antd";
import ValidationRunStatus from "@components/validationRuns/ValidationRunStatus";
import ValidationGroupDialog, {useValidationGroupDialog} from "@components/validationRuns/ValidationGroupDialog";

export default function ValidationGroup({group}) {

    const dialog = useValidationGroupDialog()

    const onClick = () => {
        dialog.start(group)
    }

    return (
        <>
            <Space className="ot-validation-group">
                <ValidationRunStatus
                    status={group}
                    text={`${group.count} ${group.statusID.name}`}
                    tooltipContent={`${group.description}. Click to get more details.`}
                    onClick={onClick}
                />
            </Space>
            <ValidationGroupDialog dialog={dialog}/>
        </>
    )
}