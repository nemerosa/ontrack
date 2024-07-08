import {Space} from "antd";
import SearchResult from "@components/search/SearchResult";

export default function SearchResultList({results}) {
    return (
        <>
            <Space direction="vertical">
                {
                    results.map((result, index) => <SearchResult
                        key={index}
                        result={result}
                    />)
                }
            </Space>
        </>
    )
}