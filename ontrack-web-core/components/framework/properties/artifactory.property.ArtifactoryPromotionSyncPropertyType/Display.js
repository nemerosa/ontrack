export default function Display({property}) {
    return (
        <>
            {property.value.buildName}
            ({property.value.buildNameFilter})
            every {property.value.interval} minute(s)
        </>
    )
}