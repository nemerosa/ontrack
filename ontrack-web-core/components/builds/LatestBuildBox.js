import {Typography} from "antd";
import TransitionBox from "@components/common/TransitionBox";
import BuildLink from "@components/builds/BuildLink";

export default function LatestBuildBox({build}) {
    return (
        <>
            <TransitionBox
                before={<Typography.Text type="secondary" italic>Latest</Typography.Text>}
                after={<BuildLink build={build}/>}
            />
        </>
    )
}