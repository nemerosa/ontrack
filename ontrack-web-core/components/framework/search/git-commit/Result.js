import SearchResultComponent from "@components/framework/search/SearchResultComponent";
import {Space, Typography} from "antd";
import Link from "next/link";

export default function Result({data}) {
    return <SearchResultComponent
        title={
            <>
                <Space>
                    <Link href={`/extension/git/${data.item.projectId}/commit-info/${data.item.commit}`}>
                        <Typography.Text code>{data.item.commitShort}</Typography.Text>
                    </Link>
                </Space>
            </>
        }
        description={data.item.commitMessage}
    />
}
