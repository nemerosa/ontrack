import {Input} from "antd";
import {useRouter} from "next/router";

export default function NavBarSearch({style}) {

    const router = useRouter()

    const onSearch = async (value) => {
        await router.push(`/search?q=${encodeURIComponent(value)}`)
    }

    return (
        <>
            <Input.Search
                style={style}
                placeholder="Search..."
                onSearch={onSearch}
            />
        </>
    )
}