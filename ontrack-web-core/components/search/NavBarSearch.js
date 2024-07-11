import {Input} from "antd";
import {useSearch} from "@/pages/search";
import {useContext} from "react";
import {SearchContext} from "@components/search/SearchContext";

export default function NavBarSearch({style}) {

    const search = useSearch()
    const {active} = useContext(SearchContext)

    return (
        <>
            {
                !active &&
                <Input.Search
                    style={style}
                    placeholder="Search..."
                    onSearch={search}
                    allowClear={true}
                />
            }
        </>
    )
}