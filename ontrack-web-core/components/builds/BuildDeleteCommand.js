import ConfirmCommand from "@components/common/ConfirmCommand";
import {FaTrash} from "react-icons/fa";
import {Typography} from "antd";
import {gql} from "graphql-request";
import {useRouter} from "next/router";
import {branchUri} from "@components/common/Links";
import {useBuild} from "@components/services/fragments";

export default function BuildDeleteCommand({id}) {

    const {build} = useBuild(id)

    const router = useRouter()

    const onSuccess = () => {
        router.push(branchUri(build.branch))
    }

    return (
        <>
            <ConfirmCommand
                icon={<FaTrash/>}
                text="Delete build"
                confirmTitle={`Do you really want to delete the "${build?.name}" build?`}
                confirmText={
                    <Typography.Text>All data associated with the <b>{build?.name}</b> build will be
                        gone. This cannot be cancelled.</Typography.Text>
                }
                confirmOkText="Delete"
                confirmOkType="danger"
                gqlQuery={
                    gql`
                        mutation DeleteBuild($id: Int!) {
                            deleteBuildById(input: {id: $id}) {
                                errors {
                                    message
                                }
                            }
                        }
                    `
                }
                gqlVariables={{id: Number(id)}}
                gqlUserNode="deleteBuildById"
                onSuccess={onSuccess}
            />
        </>
    )
}