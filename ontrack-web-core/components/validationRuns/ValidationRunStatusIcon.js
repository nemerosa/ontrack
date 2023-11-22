import Image from "next/image";
import {homeHref} from "@components/common/Links";

export default function ValidationRunStatusIcon({statusID}) {
    return <Image
        src={homeHref(`validationRunStatus/${statusID.id}.png`)}
        alt={`Validation status for ${statusID.id}`}
        width={24}
        height={24}
    />
}