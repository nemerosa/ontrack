export default function ValidationRunStatusIcon({statusID}) {
    return <img
        src={`/ui/validationRunStatus/${statusID.id}.png`}
        alt={`Validation status for ${statusID.id}`}
        width={24}
        height={24}
    />
}