import SearchResultComponent from "@components/framework/search/SearchResultComponent";
import {Space, Typography} from "antd";
import Link from "next/link";

export default function Result({data}) {
    return <SearchResultComponent
        title={
            <>
                <Space>
                    <Link href={`/extension/git/${data.item.projectId}/issue-info/${data.item.key}`}>
                        <Typography.Text code>{data.item.displayKey}</Typography.Text>
                    </Link>
                </Space>
            </>
        }
        description=""
    />
}