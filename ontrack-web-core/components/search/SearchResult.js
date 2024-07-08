import {Dynamic} from "@components/common/Dynamic";
import {Space} from "antd";
import SearchResultType from "@components/search/SearchResultType";
import SearchResultAccuracy from "@components/search/SearchResultAccuracy";

export default function SearchResult({result}) {
    return (
        <>
            <Space>
                <Space direction="vertical" size={8}>
                    <SearchResultType type={result.type}/>
                    <SearchResultAccuracy accuracy={result.accuracy}/>
                </Space>
                <Dynamic path={`framework/search/${result.type.id}/Result`} props={result}/>
            </Space>
        </>
    )
}