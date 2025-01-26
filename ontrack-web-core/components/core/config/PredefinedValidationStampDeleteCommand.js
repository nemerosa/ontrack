import {FaTrash} from "react-icons/fa";
import {Typography} from "antd";
import {gql} from "graphql-request";
import ConfirmCommand from "@components/common/ConfirmCommand";

export default function PredefinedValidationStampDeleteCommand({pvs, onChange}) {

    return (
        <>
            <ConfirmCommand
                icon={<FaTrash/>}
                title="Delete predefined validation stamp"
                confirmTitle={`Do you really want to delete the "${pvs?.name}" predefined validation stamp?`}
                confirmText={
                    <Typography.Text>All data associated with the <b>{pvs?.name}</b> predefined validation stamp will be
                        gone. This cannot be cancelled.</Typography.Text>
                }
                confirmOkText="Delete"
                confirmOkType="danger"
                gqlQuery={
                    gql`
                        mutation DeletePredefinedValidationStamp($id: Int!) {
                            deletePredefinedValidationStamp(input: {id: $id}) {
                                errors {
                                    message
                                }
                            }
                        }
                    `
                }
                gqlVariables={{id: pvs?.id}}
                gqlUserNode="deletePredefinedValidationStamp"
                onSuccess={onChange}
            />
        </>
    )
}