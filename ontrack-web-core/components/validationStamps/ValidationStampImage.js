import {restValidationStampImageUri} from "@components/common/Links";
import {FaWrench} from "react-icons/fa";
import LegacyImage from "@components/common/LegacyImage";
import {EventsContext} from "@components/common/EventsContext";
import {useContext, useState} from "react";
import ProxyImage from "@components/common/ProxyImage";

export default function ValidationStampImage({validationStamp, size = 16}) {

    const [refresh, setRefresh] = useState(0)
    const eventsContext = useContext(EventsContext)
    eventsContext.subscribeToEvent("validationStamp.image", ({id}) => {
        if (Number(id) === validationStamp.id) {
            setRefresh(refresh + 1)
        }
    })

    return (
        validationStamp.image ?
            <ProxyImage
                restUri={`${restValidationStampImageUri(validationStamp)}?key=${refresh}`}
                alt={validationStamp.name}
                width={size}
                height={size}
            /> : <FaWrench
                width={size}
                height={size}
            />
    )
}
