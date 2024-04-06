import {useEffect, useState} from "react";
import {Space, Typography} from "antd";
import {gql} from "graphql-request";
import {restPredefinedValidationStampImageUri} from "@components/common/Links";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import ProxyImage from "@components/common/ProxyImage";

export default function PredefinedValidationStampImage({name, displayName = true, size = 24}) {

    const client = useGraphQLClient()

    const [image, setImage] = useState('')

    useEffect(() => {
        if (client) {
            client.request(
                gql`
                    query PredefinedValidationStamp($name: String!) {
                        predefinedValidationStampByName(name: $name) {
                            id
                            isImage
                        }
                    }
                `, {name}
            ).then(data => {
                const pvs = data.predefinedValidationStampByName
                if (pvs && pvs.isImage) {
                    setImage(
                        <ProxyImage
                            restUri={restPredefinedValidationStampImageUri(pvs)}
                            alt={`Predefined validation stamp ${name}`}
                            width={size}
                            height={size}
                        />
                    )
                } else {
                    setImage('')
                }
            })
        }
    }, [client, name])

    return (
        <Space size={8}>
            {image}
            {displayName && <Typography.Text>{name}</Typography.Text>}
        </Space>
    )
}