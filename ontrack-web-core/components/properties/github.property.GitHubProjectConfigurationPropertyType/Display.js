export default function Display({property}) {
    return (
        <a href={`${property.value.configuration.url}/${property.value.repository}`}>
            {property.value.repository}
            @ {property.value.configuration.name}
        </a>
    )
}