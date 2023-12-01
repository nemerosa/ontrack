import {Tooltip, Typography} from "antd";
import BranchLink from "@components/branches/BranchLink";

export default function BranchDisplayNameLink({branch, children}) {
    return (
        <>
            {
                branch && branch.displayName && branch.displayName !== branch.name &&
                <Tooltip title={branch.name}>
                    <Typography.Text italic ellipsis>
                        <BranchLink branch={branch} text={branch.displayName}/>
                        {children}
                    </Typography.Text>
                </Tooltip>
            }
            {
                branch && (!branch.displayName || branch.displayName === branch.name) &&
                <Typography.Text italic ellipsis>
                    <BranchLink branch={branch} text={branch.name}/>
                    {children}
                </Typography.Text>
            }
        </>
    )
}