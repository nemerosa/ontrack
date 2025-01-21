import {FaTrash} from "react-icons/fa";
import {Typography} from "antd";
import {gql} from "graphql-request";
import ConfirmCommand from "@components/common/ConfirmCommand";

export default function PredefinedPromotionLevelDeleteCommand({ppl, onChange}) {

    return (
        <>
            <ConfirmCommand
                icon={<FaTrash/>}
                title="Delete predefined promotion level"
                confirmTitle={`Do you really want to delete the "${ppl?.name}" predefined promotion level?`}
                confirmText={
                    <Typography.Text>All data associated with the <b>{ppl?.name}</b> predefined promotion level will be
                        gone. This cannot be cancelled.</Typography.Text>
                }
                confirmOkText="Delete"
                confirmOkType="danger"
                gqlQuery={
                    gql`
                        mutation DeletePredefinedPromotionLevel($id: Int!) {
                            deletePredefinedPromotionLevel(input: {id: $id}) {
                                errors {
                                    message
                                }
                            }
                        }
                    `
                }
                gqlVariables={{id: ppl?.id}}
                gqlUserNode="deletePredefinedPromotionLevel"
                onSuccess={onChange}
            />
        </>
    )
}