import {FaBullseye} from "react-icons/fa";
import {message, Modal} from "antd";
import {gql} from "graphql-request";
import ConfirmCommand from "@components/common/ConfirmCommand";

export default function PromotionLevelBulkUpdateCommand({id}) {

    const onSuccess = () => {
        message.info("Bulk update done.")
    }

    return (
        <>
            <ConfirmCommand
                icon={<FaBullseye/>}
                text="Bulk update"
                confirmTitle="Promotion levels bulk update"
                confirmText="Updates all other promotion levels with the same name?"
                gqlQuery={
                    gql`
                        mutation PromotionLevelBulkUpdate($id: Int!) {
                            bulkUpdatePromotionLevelById(input: {id: $id}) {
                                errors {
                                    message
                                }
                            }
                        }
                    `
                }
                gqlVariables={{id}}
                gqlUserNode="bulkUpdatePromotionLevelById"
                onSuccess={onSuccess}
            />
        </>
    )
}