import Image from "next/image";
import {legacyValidationStampImageUri} from "@components/common/Links";
import {FaWrench} from "react-icons/fa";
import LegacyImage from "@components/common/LegacyImage";

export default function ValidationStampImage({validationStamp, size = 16}) {
    return (
        validationStamp.image ?
            <LegacyImage
                href={legacyValidationStampImageUri(validationStamp)}
                alt={validationStamp.name}
                width={size}
                height={size}
            /> : <FaWrench
                width={size}
                height={size}
            />
    )
}
