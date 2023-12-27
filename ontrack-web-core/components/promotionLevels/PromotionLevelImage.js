import {legacyPromotionLevelImageUri} from "@components/common/Links";
import LegacyImage from "@components/common/LegacyImage";
import {useContext, useState} from "react";
import {EventsContext} from "@components/common/EventsContext";

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
            <LegacyImage href={`${legacyPromotionLevelImageUri(promotionLevel)}?key=${refresh}`}
                         alt={promotionLevel.name}
                         width={size}
                         height={size}
            /> : undefined
    )
}