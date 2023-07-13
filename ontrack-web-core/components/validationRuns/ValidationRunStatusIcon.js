import Image from "next/image";

export default function ValidationRunStatusIcon({statusID}) {
    return <Image
        src={`/validationRunStatus/${statusID.id}.png`}
        alt={statusID.name}
        width={24}
        height={24}
    />
}