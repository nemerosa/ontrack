import {Space} from "antd";
import {gql} from "graphql-request";
import {useContext} from "react";
import {PromotionLevelImage} from "@components/promotionLevels/PromotionLevelImage";
import {EventsContext} from "@components/common/EventsContext";
import ChangeImageDialog, {useChangeImageDialog} from "@components/common/ChangeImageDialog";

export const usePromotionLevelChangeImageDialog = () => {

    const eventsContext = useContext(EventsContext)

    return useChangeImageDialog({
        query: gql`
            query PromotionLevel($id: Int!) {
                promotionLevel(id: $id) {
                    id
                    name
                    image
                }
            }
        `,
        queryUserNode: 'promotionLevel',
        imageCallback: (data, id) => {
            fetch(`/api/protected/images/promotionLevels/${id}`, {
                method: 'PUT',
                body: data,
            }).then(() => {
                eventsContext.fireEvent("promotionLevel.image", {id})
            })
        }
    })
}

export default function PromotionLevelChangeImageDialog({promotionLevelChangeImageDialog}) {

    const renderImage = (promotionLevel) => {
        return (
            <>
                <Space>
                    {
                        promotionLevel &&
                        <PromotionLevelImage promotionLevel={promotionLevel}/>
                    }
                    Image
                </Space>
            </>
        )
    }

    return (
        <>
            <ChangeImageDialog
                changeImageDialog={promotionLevelChangeImageDialog}
                renderer={renderImage}
            />
        </>
    )
}
