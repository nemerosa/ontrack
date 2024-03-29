import {Space} from "antd";
import {useRestClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {useContext} from "react";
import {EventsContext} from "@components/common/EventsContext";
import ChangeImageDialog, {useChangeImageDialog} from "@components/common/ChangeImageDialog";
import ValidationStampImage from "@components/validationStamps/ValidationStampImage";

export const useValidationStampChangeImageDialog = () => {

    const restClient = useRestClient()
    const eventsContext = useContext(EventsContext)

    return useChangeImageDialog({
        query: gql`
            query ValidationStamp($id: Int!) {
                validationStamp(id: $id) {
                    id
                    name
                    image
                }
            }
        `,
        queryUserNode: 'validationStamp',
        imageCallback: (data, id) => {
            restClient.put(`/rest/structure/validationStamps/${id}/image`, data).then(() => {
                eventsContext.fireEvent("validationStamp.image", {id})
            })
        }
    })
}

export default function ValidationStampChangeImageDialog({validationStampChangeImageDialog}) {

    const renderImage = (validationStamp) => {
        return (
            <>
                <Space>
                    {
                        validationStamp &&
                        <ValidationStampImage validationStamp={validationStamp}/>
                    }
                    Image
                </Space>
            </>
        )
    }

    return (
        <>
            <ChangeImageDialog
                changeImageDialog={validationStampChangeImageDialog}
                renderer={renderImage}
            />
        </>
    )
}
