import {Button} from "antd";
import {FaExchangeAlt} from "react-icons/fa";
import {scmChangeLogUri} from "@components/common/Links";

export default function ChangelogButton({targetBuild, lastTargetBuild}) {
    console.log({targetBuild})
    return (
        <>
            {
                lastTargetBuild && targetBuild &&
                lastTargetBuild.id !== targetBuild.id &&
                lastTargetBuild.branch?.scmBranchInfo?.changeLogs &&
                <Button
                    type="link"
                    icon={<FaExchangeAlt/>}
                    title={`Changelog between ${targetBuild.displayName} and ${lastTargetBuild.displayName}`}
                    href={scmChangeLogUri(targetBuild.id, lastTargetBuild.id)}
                />
            }
        </>
    )
}