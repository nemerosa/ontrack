import ConfirmCommand from "@components/common/ConfirmCommand";
import {FaTrash} from "react-icons/fa";
import {Typography} from "antd";
import {gql} from "graphql-request";
import {useRouter} from "next/router";
import {homeUri} from "@components/common/Links";
import {useProject} from "@components/services/fragments";

export default function ProjectDeleteCommand({id}) {

    const {project} = useProject(id)

    const router = useRouter()

    const onSuccess = () => {
        router.push(homeUri())
    }

    return (
        <>
            <ConfirmCommand
                icon={<FaTrash/>}
                text="Delete project"
                confirmTitle={`Do you really want to delete the "${project?.name}" project?`}
                confirmText={
                    <Typography.Text>All data associated with the <b>{project?.name}</b> project will be
                        gone. This cannot be cancelled.</Typography.Text>
                }
                confirmOkText="Delete"
                confirmOkType="danger"
                gqlQuery={
                    gql`
                        mutation DeleteProject($id: Int!) {
                            deleteProject(input: {id: $id}) {
                                errors {
                                    message
                                }
                            }
                        }
                    `
                }
                gqlVariables={{id: Number(id)}}
                gqlUserNode="deleteProject"
                onSuccess={onSuccess}
            />
        </>
    )
}