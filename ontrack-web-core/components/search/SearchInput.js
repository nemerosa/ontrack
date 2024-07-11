import {Input} from "antd";
import {useEffect, useState} from "react";
import {useSearch} from "@/pages/search";

export default function SearchInput({q, searching}) {

    const [searchValue, setSearchValue] = useState(q)
    useEffect(() => {
        setSearchValue(q)
    }, [q])

    const search = useSearch()

    return (
        <>
            <Input.Search
                size="large"
                disabled={searching}
                loading={searching}
                onSearch={search}
                value={searchValue}
                onChange={(e) => setSearchValue(e.target.value)}
            />
        </>
    )
}