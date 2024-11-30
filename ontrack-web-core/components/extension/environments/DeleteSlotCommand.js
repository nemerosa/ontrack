import {FaTrash} from "react-icons/fa";
import {isAuthorized} from "@components/common/authorizations";
import ConfirmCommand from "@components/common/ConfirmCommand";
import {Typography} from "antd";
import {gql} from "graphql-request";
import {useRouter} from "next/router";
import {environmentsUri} from "@components/extension/environments/EnvironmentsLinksUtils";

export default function DeleteSlotCommand({slot}) {

    const router = useRouter()

    const onSuccess = async () => {
        await router.push(environmentsUri)
    }

    return (
        <>
            {
                isAuthorized(slot, 'slot', 'delete') &&
                <ConfirmCommand
                    icon={<FaTrash/>}
                    text="Delete slot"
                    confirmTitle="Deleting slot"
                    confirmText={
                        <Typography.Text>
                            All data associated with the slot will be
                            gone. This cannot be cancelled. Do you really want to delete this slot?
                        </Typography.Text>
                    }
                    confirmOkText="Delete"
                    confirmOkType="danger"
                    gqlQuery={
                        gql`
                            mutation DeleteSlot($id: String!) {
                                deleteSlot(input: {slotId: $id}) {
                                    errors {
                                        message
                                    }
                                }
                            }
                        `
                    }
                    gqlVariables={{id: slot.id}}
                    gqlUserNode="deleteSlot"
                    onSuccess={onSuccess}
                />
            }
        </>
    )
}