import {Button, Popconfirm, Typography} from "antd";
import {FaRedo} from "react-icons/fa";
import {useMutation} from "@components/services/GraphQL";
import {gql} from "graphql-request";
import {useRouter} from "next/router";

export default function AutoVersioningAuditEntryReschedule({entry}) {

    const router = useRouter()

    const {mutate, loading} = useMutation(
        gql`
            mutation RescheduleAutoVersioningAuditEntry($uuid: String!) {
                rescheduleAutoVersioning(input: {uuid: $uuid}) {
                    order {
                        uuid
                    }
                    errors {
                        message
                    }
                }
            }
        `,
        {
            userNodeName: 'rescheduleAutoVersioning',
            onSuccess: async (userNode) => {
                await router.push(`/extension/auto-versioning/audit/detail/${userNode.order.uuid}`)
            }
        }
    )

    const reschedule = async () => {
        await mutate({uuid: entry.order.uuid})
    }

    return (
        <>
            <Popconfirm
                title="Reschedule auto-versioning"
                description="Are you sure to reschedule this auto-versioning request?"
                okText="Yes"
                cancelText="No"
                onConfirm={reschedule}
            >
                <Button disabled={loading} icon={<FaRedo/>}>
                    <Typography.Text>Reschedule</Typography.Text>
                </Button>
            </Popconfirm>
        </>
    )
}