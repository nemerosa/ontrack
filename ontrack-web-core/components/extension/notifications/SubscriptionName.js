import {message, Spin, Typography} from "antd";
import {useState} from "react";
import {FaPencilAlt} from "react-icons/fa";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {getUserErrors} from "@components/services/graphql-utils";

export default function SubscriptionName({subscription, entity, managePermission, onRenamed}) {

    const client = useGraphQLClient()
    const [messageApi, contextHolder] = message.useMessage()
    const [changing, setChanging] = useState(false)

    const onChange = async (value) => {
        const data = await client.request(
            gql`
                mutation RenameSubscription(
                    $projectEntity: ProjectEntityIDInput,
                    $name: String!,
                    $newName: String!,
                ) {
                    renameSubscription(input: {
                        projectEntity: $projectEntity,
                        name: $name,
                        newName: $newName,
                    }) {
                        errors {
                            message
                        }
                    }
                }
            `,
            {
                projectEntity: entity,
                name: subscription.name,
                newName: value,
            }
        )
        const errors = getUserErrors(data.renameSubscription)
        if (errors) {
            messageApi.error(errors[0])
        } else if (onRenamed) {
            onRenamed(value)
        }
    }

    return (
        <>
            {contextHolder}
            <Typography.Text
                editable={managePermission ? {
                    onChange: onChange,
                    icon: changing ? <Spin size="small"/> : <FaPencilAlt size={12}/>,
                } : false}
            >
                {subscription.name}
            </Typography.Text>
        </>
    )
}