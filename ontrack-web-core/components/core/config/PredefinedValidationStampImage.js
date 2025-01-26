import {restPredefinedValidationStampImageUri} from "@components/common/Links";
import ProxyImage from "@components/common/ProxyImage";
import GeneratedIcon from "@components/common/icons/GeneratedIcon";

export default function PredefinedValidationStampImage({predefinedValidationStamp, title, size = 24}) {
    return (
        <>
            {
                predefinedValidationStamp.isImage ?
                    <ProxyImage
                        restUri={restPredefinedValidationStampImageUri(predefinedValidationStamp)}
                        alt={title}
                        width={size}
                        height={size}
                    /> : <GeneratedIcon
                        name={predefinedValidationStamp.name}
                        colorIndex={predefinedValidationStamp.id}
                        size={size}
                    />
            }
        </>
    )
}