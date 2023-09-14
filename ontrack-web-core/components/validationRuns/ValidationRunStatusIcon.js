import Image from "next/image";

export default function ValidationRunStatusIcon({statusID}) {
    return <Image
        src={`/validationRunStatus/${statusID.id}.png`}
        alt={`Validation status for ${statusID.id}`}
        width={24}
        height={24}
    />
}