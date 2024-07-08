import {Space, Typography} from "antd";
import SearchResultTypeIcon from "@components/search/SearchResultTypeIcon";

export default function SearchResultType({type}) {
    return (
        <>
            <Space>
                <SearchResultTypeIcon type={type}/>
                {type.name}
                <Typography.Text type="secondary" italic>{type.description}</Typography.Text>
            </Space>
        </>
    )
}