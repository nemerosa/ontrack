import {Input} from "antd";
import {useSearch} from "@/pages/search";

export default function NavBarSearch({style}) {

    const search = useSearch()

    return (
        <>
            <Input.Search
                style={style}
                placeholder="Search..."
                onSearch={search}
            />
        </>
    )
}