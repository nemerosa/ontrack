import ConfirmCommand from "@components/common/ConfirmCommand";
import {FaTrash} from "react-icons/fa";
import {Typography} from "antd";
import {gql} from "graphql-request";
import {useRouter} from "next/router";
import {projectUri} from "@components/common/Links";
import {useBranch} from "@components/services/fragments";

export default function BranchDeleteCommand({id}) {

    const {branch} = useBranch(id)

    const router = useRouter()

    const onSuccess = () => {
        router.push(projectUri(branch.project))
    }

    return (
        <>
            <ConfirmCommand
                icon={<FaTrash/>}
                text="Delete branch"
                confirmTitle={`Do you really want to delete the "${branch?.name}" branch?`}
                confirmText={
                    <Typography.Text>All data associated with the <b>{branch?.name}</b> branch will be
                        gone. This cannot be cancelled.</Typography.Text>
                }
                confirmOkText="Delete"
                confirmOkType="danger"
                gqlQuery={
                    gql`
                        mutation DeleteBranch($id: Int!) {
                            deleteBranchById(input: {id: $id}) {
                                errors {
                                    message
                                }
                            }
                        }
                    `
                }
                gqlVariables={{id}}
                gqlUserNode="deleteBranchById"
                onSuccess={onSuccess}
            />
        </>
    )
}