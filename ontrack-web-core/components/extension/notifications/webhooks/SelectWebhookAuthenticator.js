import {useQuery} from "@components/services/useQuery";
import {gql} from "graphql-request";
import {Select} from "antd";

export default function SelectWebhookAuthenticator({id, value, onChange, onSelectedWebhookAuthenticator}) {
    const {loading, data: options} = useQuery(
        gql`
            query SelectWebhookAuthenticator {
                webhookAuthenticators {
                    value: type
                    label: displayName
                }
            }
        `,
        {
            initialData: [],
            dataFn: data => data.webhookAuthenticators,
        }
    )

    const onLocalChange = (value) => {
        if (onChange) onChange(value)
        if (onSelectedWebhookAuthenticator) onSelectedWebhookAuthenticator(value)
    }

    return (
        <>
            <Select
                id={id}
                options={options}
                loading={loading}
                value={value}
                onChange={onLocalChange}
                allowClear={true}
            />
        </>
    )
}