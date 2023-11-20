import {Tooltip, Typography} from "antd";
import {branchLink} from "@components/common/Links";

export default function BranchDisplayNameLink({branch, children}) {
    return (
        <>
            {
                branch && branch.displayName && branch.displayName !== branch.name &&
                <Tooltip title={branch.name}>
                    <Typography.Text italic ellipsis>
                        {branchLink(branch, branch.displayName)}
                        {children}
                    </Typography.Text>
                </Tooltip>
            }
            {
                branch && (!branch.displayName || branch.displayName === branch.name) &&
                <Typography.Text italic ellipsis>
                    {branchLink(branch, branch.name)}
                    {children}
                </Typography.Text>
            }
        </>
    )
}