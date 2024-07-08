import {Space} from "antd";
import SearchResult from "@components/search/SearchResult";

export default function SearchResultList({type, results}) {
    return (
        <>
            <Space direction="vertical">
                {
                    results.map((result, index) => <SearchResult
                        key={index}
                        type={type}
                        result={result}
                    />)
                }
            </Space>
        </>
    )
}