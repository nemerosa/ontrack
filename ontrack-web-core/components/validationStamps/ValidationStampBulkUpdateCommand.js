import {FaBullseye} from "react-icons/fa";
import {message} from "antd";
import {gql} from "graphql-request";
import ConfirmCommand from "@components/common/ConfirmCommand";

export default function ValidationStampBulkUpdateCommand({id}) {

    const onSuccess = () => {
        message.info("Bulk update done.")
    }

    return (
        <>
            <ConfirmCommand
                icon={<FaBullseye/>}
                text="Bulk update"
                confirmTitle="Validation stamps bulk update"
                confirmText="Updates all other validation stamps with the same name?"
                gqlQuery={
                    gql`
                        mutation ValidationStampBulkUpdate($id: Int!) {
                            bulkUpdateValidationStampById(input: {id: $id}) {
                                errors {
                                    message
                                }
                            }
                        }
                    `
                }
                gqlVariables={{id}}
                gqlUserNode="bulkUpdateValidationStampById"
                onSuccess={onSuccess}
            />
        </>
    )
}