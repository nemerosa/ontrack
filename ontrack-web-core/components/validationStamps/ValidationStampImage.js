import {restValidationStampImageUri} from "@components/common/Links";
import {useEventForRefresh} from "@components/common/EventsContext";
import ProxyImage from "@components/common/ProxyImage";
import GeneratedIcon from "@components/common/icons/GeneratedIcon";

export default function ValidationStampImage({validationStamp, size = 16}) {

    const refreshCount = useEventForRefresh("validationStamp.image")

    return (
        validationStamp.image ?
            <ProxyImage
                id={`validation-stamp-image-${validationStamp.id}`}
                restUri={`${restValidationStampImageUri(validationStamp)}?key=${refreshCount}`}
                alt={validationStamp.name}
                width={size}
                height={size}
            /> : <GeneratedIcon
                name={validationStamp.name}
                colorIndex={validationStamp.id}
            />
    )
}
