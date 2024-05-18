export default function Display({property}) {
    return (
        <a href={property.value.repositoryUrl}>{property.value.repositoryUrl}</a>
    )
}