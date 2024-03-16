import Link from "next/link";
import RunInfoSourceTypeIcon from "@components/common/RunInfoSourceTypeIcon";
import {Space} from "antd";

export default function RunInfoSource({info}) {
    return (
        <>
            {
                info.sourceType && info.sourceUri &&
                <Space>
                    <RunInfoSourceTypeIcon type={info.sourceType}/>
                    <Link href={info.sourceUri}>{info.sourceType}</Link>
                </Space>
            }
        </>
    )
}