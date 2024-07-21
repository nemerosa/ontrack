import {Dynamic} from "@components/common/Dynamic";
import {Typography} from "antd";

export default function AutoVersioningPostProcessing({type, config}) {
    return (
        <>
            {
                !type && <Typography.Text type="secondary">None</Typography.Text>
            }
            {
                type &&
                <Dynamic
                    path={`framework/auto-versioning-post-processing/${type}/Display`}
                    props={config}
                />
            }
        </>
    )
}