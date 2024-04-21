import {Tooltip, Typography} from "antd";

export default function AutoVersioningApproval({autoApproval, autoApprovalMode}) {

    // Auto approval is the default
    const actualAutoApproval = autoApproval !== undefined ? autoApproval : true
    // CLIENT auto approval is the default
    const actualAutoApprovalMode = autoApprovalMode ?? 'CLIENT'

    return (
        <>
            {
                actualAutoApproval && actualAutoApprovalMode === 'CLIENT' &&
                <Tooltip
                    title="The auto-versioning PR will be controlled and merged by Ontrack (client of the SCM)."
                >
                    <Typography.Text>Auto (client)</Typography.Text>
                </Tooltip>
            }
            {
                actualAutoApproval && actualAutoApprovalMode === 'SCM' &&
                <Tooltip
                    title="The auto-versioning PR will be created by Ontrack but merged automatically by the SCM."
                >
                    <Typography.Text>Auto (SCM)</Typography.Text>
                </Tooltip>
            }
            {
                !actualAutoApproval &&
                <Tooltip
                    title="The auto-versioning PR will be created by Ontrack but needs to be approved and merged manually."
                >
                    <Typography.Text type="secondary">No</Typography.Text>
                </Tooltip>
            }
        </>
    )
}