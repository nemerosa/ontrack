import {buildUri} from "@components/common/Links";
import {buildKnownName} from "@components/common/Titles";
import Link from "next/link";
import {Popover, Space, Typography} from "antd";

export default function BuildLink({build, text, displayTooltip}) {
    return (
        <>
            {
                displayTooltip && <Popover content={
                    <Space direction="vertical">
                        <Typography.Text>Link to the build</Typography.Text>
                        <Typography.Text>
                            {build.name}
                            {
                                build.releaseProperty?.value &&
                                <Typography.Text strong> ({build.releaseProperty.value.name})</Typography.Text>
                            }
                        </Typography.Text>
                    </Space>
                }>
                    <Link
                        href={buildUri(build)}
                    >{text ?? buildKnownName(build)}</Link>
                </Popover>
            }
            {
                !displayTooltip && <Link
                    href={buildUri(build)}
                    title="Link to build page"
                >{text ?? buildKnownName(build)}</Link>
            }
        </>
    )
}