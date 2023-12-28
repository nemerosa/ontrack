import {Command} from "@components/common/Commands";
import {FaTrash} from "react-icons/fa";
import {Modal, Space, Typography} from "antd";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {getUserErrors} from "@components/services/graphql-utils";
import {useEffect, useState} from "react";
import FormErrors from "@components/form/FormErrors";
import {getPromotionLevelById} from "@components/services/fragments";
import {useRouter} from "next/router";
import {branchUri} from "@components/common/Links";

const {confirm} = Modal

export default function PromotionLevelDeleteCommand({id}) {

    const client = useGraphQLClient()

    const [promotionLevel, setPromotionLevel] = useState()
    useEffect(() => {
        if (client && id) {
            getPromotionLevelById(client, id).then(pl => setPromotionLevel(pl))
        }
    }, [client, id]);

    const [errors, setErrors] = useState([])
    const router = useRouter()

    const onAction = () => {
        console.log("On delete...")
        confirm({
            title: "Do you really want to delete this promotion level?",
            content: <Space direction="vertical">
                <Typography.Text>All data associated with the <b>{promotionLevel.name}</b> promotion level will be gone. This cannot be cancelled.</Typography.Text>
                <FormErrors errors={errors}/>
            </Space>,
            okText: "Delete",
            okType: "danger",
            onCancel: () => {
            },
            onOk: (close) => {
                return client.request(
                    gql`
                        mutation DeletePromotionLevel($id: Int!) {
                            deletePromotionLevelById(input: {id: $id}) {
                                errors {
                                    message
                                }
                            }
                        }
                    `,
                    {id}
                ).then(data => {
                    const errors = getUserErrors(data.deletePromotionLevelById)
                    if (errors) {
                        setErrors(errors)
                    } else {
                        close()
                        // Going back to the branch
                        router.push(branchUri(promotionLevel.branch))
                    }
                })
            },
        })
    }

    return (
        <>
            <Command
                icon={<FaTrash/>}
                text="Delete promotion level"
                action={onAction}
            />
        </>
    )
}