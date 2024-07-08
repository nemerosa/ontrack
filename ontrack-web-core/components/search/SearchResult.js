import {Dynamic} from "@components/common/Dynamic";
import {Space} from "antd";
import SearchResultType from "@components/search/SearchResultType";

export default function SearchResult({result}) {
    return (
        <>
            <Space>
                <SearchResultType type={result.type}/>
                <Dynamic path={`framework/search/${result.type.id}/Result`} props={result}/>
            </Space>
        </>
    )
}