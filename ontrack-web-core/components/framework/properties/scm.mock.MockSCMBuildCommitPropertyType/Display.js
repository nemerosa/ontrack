export default function Display({property}) {
    return (
        <pre>
            {JSON.stringify(property.value, null, 2)}
        </pre>
    )
}