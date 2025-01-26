import {Space} from "antd";
import {useRestClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import ChangeImageDialog, {useChangeImageDialog} from "@components/common/ChangeImageDialog";
import PredefinedValidationStampImage from "@components/core/config/PredefinedValidationStampImage";

export const usePredefinedValidationStampChangeImageDialog = ({onChange}) => {

    const restClient = useRestClient()

    return useChangeImageDialog({
        query: gql`
            query ValidationStamp($id: Int!) {
                predefinedValidationStampById(id: $id) {
                    id
                    name
                    isImage
                }
            }
        `,
        queryUserNode: 'predefinedValidationStampByName',
        imageCallback: (data, id) => {
            restClient.put(`/rest/admin/predefinedValidationStamps/${id}/image`, data).then(() => {
                if (onChange) onChange()
            })
        }
    })
}

export default function PredefinedValidationStampChangeImageDialog({dialog}) {

    const renderImage = (pvs) => {
        return (
            <>
                <Space>
                    {
                        pvs &&
                        <PredefinedValidationStampImage predefinedValidationStamp={pvs}/>
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
