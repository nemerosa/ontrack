import Image from "next/image";
import {validationStampImageUri} from "@components/common/Links";
import {FaWrench} from "react-icons/fa";

export default function ValidationStampImage({validationStamp, size = 16}) {
    return (
        validationStamp.image ?
            <Image
                src={validationStampImageUri(validationStamp)}
                alt={validationStamp.name}
                width={size}
                height={size}
            /> : <FaWrench
                width={size}
                height={size}
            />
    )
}
