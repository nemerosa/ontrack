import {Space, theme, Typography} from "antd";
import BuildLink from "@components/builds/BuildLink";
import TimestampText from "@components/common/TimestampText";

/**
 * The build a checkpoint names, on every kind of checkpoint.
 *
 * "Arriving" is what the three unlike kinds have in common, so it reads the same on all of them: the
 * build, and when it got there. What became of it there is drawn by the checkpoint itself, because
 * only a validation stamp has anything to say about it.
 *
 * A checkpoint nothing has reached says so in words. Leaving the line out would make an unreached
 * checkpoint and a checkpoint whose build is still loading look alike.
 *
 * @param arrival The checkpoint's arrival, absent when nothing has reached it
 * @param nothingText What to say when nothing has reached this checkpoint
 */
export default function CheckpointArrival({arrival, nothingText = "Never reached"}) {

    const {token} = theme.useToken()

    if (!arrival) {
        return <Typography.Text type="secondary" italic>{nothingText}</Typography.Text>
    }

    return (
        <Space size={token.marginXXS}>
            <BuildLink build={arrival.build} displayTooltip={true}/>
            <Typography.Text type="secondary">
                <TimestampText value={arrival.time} relative={true}/>
            </Typography.Text>
        </Space>
    )
}
