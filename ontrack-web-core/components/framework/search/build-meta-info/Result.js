import SearchResultComponent from "@components/framework/search/SearchResultComponent";
import {extractProjectEntityInfo} from "@components/entities/ProjectEntityPageInfo";
import {Space, Typography} from "antd";
import Link from "next/link";

export default function Result({data}) {

    const {type, compositeName, href} = extractProjectEntityInfo(data.entityType, data.entity)

    return <SearchResultComponent
        title={
            <Space>
                {type}
                <Link href={href}>{compositeName}</Link>
            </Space>
        }
        description={
            <>
                <Space direction="vertical">
                    <ul>
                        {
                            Object.keys(data.metaInfoItems).map(name => (
                                <>
                                    <li key={name}>
                                        <Space>
                                            <Typography.Text strong>{name}</Typography.Text>
                                            <Typography.Text type="secondary">:&nbsp;</Typography.Text>
                                            <Typography.Text
                                                type="secondary">{data.metaInfoItems[name]}</Typography.Text>
                                        </Space>
                                    </li>
                                </>
                            ))
                        }
                    </ul>
                </Space>
            </>
        }
    />
}