import Link from "next/link";
import RunInfoSourceTypeIcon from "@components/common/RunInfoSourceTypeIcon";
import {Space} from "antd";

export default function RunInfoSource({info, mode="complete"}) {
    return (
        <>
            {
                info.sourceType && info.sourceUri && mode === "complete" &&
                <Space>
                    <RunInfoSourceTypeIcon type={info.sourceType}/>
                    <Link href={info.sourceUri}>{info.sourceType}</Link>
                </Space>
            }
            {
                info.sourceType && info.sourceUri && mode === "minimal" &&
                <Space>
                    <Link href={info.sourceUri} title={`Link to ${info.sourceType}`}><RunInfoSourceTypeIcon type={info.sourceType}/></Link>
                </Space>
            }
        </>
    )
}