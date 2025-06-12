import {Space} from "antd";
import {gql} from "graphql-request";
import ChangeImageDialog, {useChangeImageDialog} from "@components/common/ChangeImageDialog";
import PredefinedPromotionLevelImage from "@components/core/config/PredefinedPromotionLevelImage";

export const usePredefinedPromotionLevelChangeImageDialog = ({onChange}) => {

    return useChangeImageDialog({
        query: gql`
            query PromotionLevel($id: Int!) {
                predefinedPromotionLevelById(id: $id) {
                    id
                    name
                    isImage
                }
            }
        `,
        queryUserNode: 'predefinedPromotionLevelByName',
        imageCallback: (data, id) => {
            fetch(`/api/protected/images/predefinedPromotionLevels/${id}`, {
                method: 'PUT',
                body: data,
            }).then(() => {
                if (onChange) onChange()
            })
        }
    })
}

export default function PredefinedPromotionLevelChangeImageDialog({dialog}) {

    const renderImage = (ppl) => {
        return (
            <>
                <Space>
                    {
                        ppl &&
                        <PredefinedPromotionLevelImage predefinedPromotionLevel={ppl}/>
                    }
                    Image
                </Space>
            </>
        )
    }

    return (
        <>
            <ChangeImageDialog
                changeImageDialog={dialog}
                renderer={renderImage}
            />
        </>
    )
}
