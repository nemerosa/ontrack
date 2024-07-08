import {Popover, Typography} from "antd";
import SearchResultTypeIcon from "@components/search/SearchResultTypeIcon";

export default function SearchResultType({type}) {
    return (
        <>
            <Popover
                title={type.name}
                content={<Typography.Text type="secondary">{type.description}</Typography.Text>}
            >
                <div>
                    <SearchResultTypeIcon type={type}/>
                </div>
            </Popover>
        </>
    )
}