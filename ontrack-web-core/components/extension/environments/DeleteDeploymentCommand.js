import {isAuthorized} from "@components/common/authorizations";
import {FaTrash} from "react-icons/fa";
import {Typography} from "antd";
import ConfirmCommand from "@components/common/ConfirmCommand";
import {gql} from "graphql-request";
import {useRouter} from "next/router";
import {slotUri} from "@components/extension/environments/EnvironmentsLinksUtils";

export default function DeleteDeploymentCommand({deployment}) {

    const router = useRouter()

    const onSuccess = async () => {
        await router.push(slotUri(deployment.slot))
    }

    return (
        <>
            {
                isAuthorized(deployment.slot, "pipeline", "delete") &&
                <ConfirmCommand
                    icon={<FaTrash/>}
                    text="Delete deployment"
                    confirmTitle="Deleting deployment"
                    confirmText={
                        <Typography.Text>
                            All data associated with the deployment will be
                            gone. This cannot be cancelled. Do you really want to delete this deployment?
                        </Typography.Text>
                    }
                    confirmOkText="Delete"
                    confirmOkType="danger"
                    gqlQuery={
                        gql`
                            mutation DeleteDeployment($id: String!) {
                            deleteDeployment(input: {deploymentId: $id}) {
                                    errors {
                                        message
                                    }
                                }
                            }
                        `
                    }
                    gqlVariables={{id: deployment.id}}
                    gqlUserNode="deleteDeployment"
                    onSuccess={onSuccess}
                />
            }
        </>
    )
}