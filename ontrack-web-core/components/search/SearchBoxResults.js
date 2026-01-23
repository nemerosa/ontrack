import {useRefData} from "@components/providers/RefDataProvider";
import SearchBoxTypeResults from "@components/search/SearchBoxTypeResults";
import {Divider, Space} from "antd";
import React from "react";

export default function SearchBoxResults({query}) {
    const {searchResultTypes} = useRefData();

    if (searchResultTypes.length === 0 || !query || query.length < 3) {
        return null;
    }

    return (
        <Space direction="vertical" style={{width: '100%'}} split={<Divider style={{margin: 0}}/>}>
            {
                searchResultTypes.map(type => (
                    <SearchBoxTypeResults
                        key={type.id}
                        type={type}
                        query={query}
                    />
                ))
            }
        </Space>
    )
}
