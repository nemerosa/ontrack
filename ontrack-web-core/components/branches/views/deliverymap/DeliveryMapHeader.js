import {Space, theme, Typography} from "antd";
import BuildLink from "@components/builds/BuildLink";
import TimestampText from "@components/common/TimestampText";

/**
 * The branch's latest build, above the map.
 *
 * The frame of reference for every lag marker on the map: a checkpoint saying "build 42" means
 * little until you know the branch is at 47. It is stated ONCE here rather than as a node of the
 * graph - an edge from the branch's latest build to every checkpoint would mean neither *unlocks*
 * nor *requires*, and would fan out across the whole map at once.
 *
 * Labelled "Latest build", which is what the pipeline view's own stat calls it. The two views name
 * one fact one way; *branch head* is the word for it in `CONTEXT.md` and in the code.
 *
 * @param head The branch's latest build, null on a branch with no build at all
 */
export default function DeliveryMapHeader({head}) {

    const {token} = theme.useToken()

    return (
        <Space size={token.marginXS} data-testid="delivery-map-header">
            <Typography.Text
                type="secondary"
                title="Each checkpoint below says how far behind this build it is"
            >
                Latest build
            </Typography.Text>
            {
                head ?
                    <>
                        <BuildLink build={head} displayTooltip={true}/>
                        {
                            head.creation?.time &&
                            <Typography.Text type="secondary">
                                <TimestampText value={head.creation.time} relative={true}/>
                            </Typography.Text>
                        }
                    </> :
                    // Said in words rather than left blank: it is also why no checkpoint on the map
                    // carries a lag marker, and the reader should not have to guess at the link
                    <Typography.Text type="secondary" italic>No build on this branch yet</Typography.Text>
            }
        </Space>
    )
}
