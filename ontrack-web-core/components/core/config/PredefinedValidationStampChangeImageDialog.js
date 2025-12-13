import {Space} from "antd";
import {gql} from "graphql-request";
import ChangeImageDialog, {useChangeImageDialog} from "@components/common/ChangeImageDialog";
import PredefinedValidationStampImage from "@components/core/config/PredefinedValidationStampImage";

export const usePredefinedValidationStampChangeImageDialog = ({onChange}) => {

    return useChangeImageDialog({
        query: gql`
            query PredefinedValidationStamp($id: Int!) {
                predefinedValidationStampById(id: $id) {
                    id
                    name
                    isImage
                }
            }
        `,
        queryUserNode: 'predefinedValidationStampById',
        imageCallback: (data, id) => {
            fetch(`/api/protected/images/predefinedValidationStamps/${id}`, {
                method: 'PUT',
                body: data,
            }).then(() => {
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
