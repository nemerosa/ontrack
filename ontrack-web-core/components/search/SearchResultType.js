import {Popover, Space, Typography} from "antd";
import SearchResultTypeIcon from "@components/search/SearchResultTypeIcon";

export default function SearchResultType({type, displayName = false, popover = true}) {
    return (
        <>
            {
                popover &&
                <Popover
                    title={type.name}
                    content={<Typography.Text type="secondary">{type.description}</Typography.Text>}
                >
                    <Space>
                        <SearchResultTypeIcon type={type}/>
                        {displayName && type.name}
                    </Space>
                </Popover>
            }
            {
                !popover && <Space>
                    <SearchResultTypeIcon type={type}/>
                    {displayName && type.name}
                </Space>
            }
        </>
    )
}