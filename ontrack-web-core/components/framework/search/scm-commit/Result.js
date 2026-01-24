import SearchResultComponent from "@components/framework/search/SearchResultComponent";
import {Space, Typography} from "antd";
import Link from "next/link";
import ProjectLinkByName from "@components/projects/ProjectLinkByName";

export default function Result({data}) {
    return <SearchResultComponent
        title={
            <>
                <Space size="small">
                    <Link href={`/extension/scm/${data.item.projectName}/commit-info/${data.item.id}`}>
                        <Typography.Text code>{data.item.shortId}</Typography.Text>
                    </Link>
                    <Typography.Text>
                        (<ProjectLinkByName name={data.item.projectName}/>)
                    </Typography.Text>
                </Space>
            </>
        }
        description=""
    />
}
