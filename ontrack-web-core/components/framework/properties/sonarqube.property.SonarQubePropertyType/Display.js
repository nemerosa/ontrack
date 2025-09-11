import Link from "next/link";

export default function Display({property}) {
    return (
        <>
            {property.value.configuration.name} (<Link
            href={property.value.configuration.url}>{property.value.configuration.url}</Link>)
        </>
    )
}