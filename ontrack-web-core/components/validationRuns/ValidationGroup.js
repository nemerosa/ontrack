import {Space, Typography} from "antd";
import ValidationRunStatus from "@components/validationRuns/ValidationRunStatus";
import ValidationGroupDialog, {useValidationGroupDialog} from "@components/validationRuns/ValidationGroupDialog";
import ValidationStampImage from "@components/validationStamps/ValidationStampImage";
import ValidationRunHistoryDialog, {
    useValidationRunHistoryDialog
} from "@components/validationRuns/ValidationRunHistoryDialog";

export default function ValidationGroup({group}) {

    const dialog = useValidationGroupDialog()
    const validationRunHistoryDialog = useValidationRunHistoryDialog()

    const onClick = () => {
        dialog.start(group)
    }

    const showRunHistory = (run) => {
        validationRunHistoryDialog.start(run)
    }

    return (
        <>
            <Space className="ot-validation-group">
                {
                    group.count > 1 &&
                    <ValidationRunStatus
                        status={group}
                        text={`${group.count} ${group.statusID.name}`}
                        tooltipContent={`${group.description}. Click to get more details.`}
                        onClick={onClick}
                    />
                }
                {
                    group.count === 1 &&
                    <ValidationRunStatus
                        status={group}
                        text={
                            <Space>
                                <ValidationStampImage
                                    validationStamp={group.validations[0].validationStamp}
                                />
                                <Typography.Text>{group.validations[0].validationStamp.name}</Typography.Text>
                                <Typography.Text>{group.statusID.name}</Typography.Text>
                            </Space>
                        }
                        tooltipContent={group.description}
                        onClick={() => showRunHistory(group.validations[0].validationRuns[0])}
                    />
                }
            </Space>
            <ValidationGroupDialog dialog={dialog}/>
            <ValidationRunHistoryDialog dialog={validationRunHistoryDialog}/>
        </>
    )
}