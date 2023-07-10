import {Typography} from "antd";
import TransitionBox from "@components/common/TransitionBox";
import {buildLink} from "@components/common/Links";

export default function LatestBuildBox({build}) {
    return (
        <>
            <TransitionBox
                before={<Typography.Text type="secondary" italic>Latest</Typography.Text>}
                after={buildLink(build)}
            />
        </>
    )
}