import {useEffect, useState} from "react";
import {Space, Typography} from "antd";
import {gql} from "graphql-request";
import {restPredefinedPromotionLevelImageUri} from "@components/common/Links";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import ProxyImage from "@components/common/ProxyImage";

export default function PredefinedPromotionLevelImage({name, displayName = true, size = 24}) {

    const client = useGraphQLClient()

    const [image, setImage] = useState('')

    useEffect(() => {
        if (client) {
            client.request(
                gql`
                    query PredefinedPromotionLevel($name: String!) {
                        predefinedPromotionLevelByName(name: $name) {
                            id
                            isImage
                        }
                    }
                `, {name}
            ).then(data => {
                const ppl = data.predefinedPromotionLevelByName
                if (ppl && ppl.isImage) {
                    setImage(
                        <ProxyImage
                            restUri={restPredefinedPromotionLevelImageUri(ppl)}
                            alt={`Predefined promotion level ${name}`}
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