import {Popover} from "antd";
import BranchLink from "@components/branches/BranchLink";
import {truncate} from "@components/common/ShortenedName";

export default function BranchDisplayNameLink({branch, children}) {
    return (
        <>
            {
                branch && branch.displayName && branch.displayName !== branch.name &&
                <Popover title={branch.name} content={branch.displayName}>
                    <BranchLink branch={branch} text={truncate(branch.displayName, 16)}/>
                    {children}
                </Popover>
            }
            {
                branch && (!branch.displayName || branch.displayName === branch.name) &&
                <Popover title={branch.name}>
                    <BranchLink branch={branch} text={truncate(branch.name)}/>
                    {children}
                </Popover>
            }
        </>
    )
}