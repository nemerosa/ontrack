import SearchResultComponent from "@components/framework/search/SearchResultComponent";
import {Space, Typography} from "antd";
import Link from "next/link";
import ProjectLinkByName from "@components/projects/ProjectLinkByName";

export default function Result({data}) {
    return <SearchResultComponent
        title={
            <>
                <Space>
                    <Link href={`/extension/scm/${data.item.projectName}/issue-info/${data.item.key}`}>
                        <Typography.Text code>{data.item.displayKey}</Typography.Text>
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