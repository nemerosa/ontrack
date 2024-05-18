import Link from "next/link";

export default function Display({property}) {

    return (
        <>
            <Link href={property.value.url}>{property.value.url}</Link>
        </>
    )
}