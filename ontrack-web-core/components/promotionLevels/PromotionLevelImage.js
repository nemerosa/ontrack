import {restPromotionLevelImageUri} from "@components/common/Links";
import {useContext, useState} from "react";
import {EventsContext} from "@components/common/EventsContext";
import ProxyImage from "@components/common/ProxyImage";
import GeneratedIcon from "@components/common/icons/GeneratedIcon";

export const PromotionLevelImage = ({promotionLevel, size = 16}) => {

    const [refresh, setRefresh] = useState(0)
    const eventsContext = useContext(EventsContext)
    eventsContext.subscribeToEvent("promotionLevel.image", ({id}) => {
        if (Number(id) === promotionLevel.id) {
            setRefresh(refresh + 1)
        }
    })

    return (
        promotionLevel.image ?
            <ProxyImage restUri={`${restPromotionLevelImageUri(promotionLevel)}?key=${refresh}`}
                        alt={promotionLevel.name}
                        width={size}
                        height={size}
            /> : <GeneratedIcon
                name={promotionLevel.name}
                colorIndex={promotionLevel.id}
            />
    )
}