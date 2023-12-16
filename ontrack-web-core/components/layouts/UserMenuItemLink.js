import Link from "next/link";

export default function UserMenuItemLink({item}) {
    return (
        <>
            <Link href={`/${item.extension}/${item.id}`}>{item.name}</Link>
        </>
    )
}