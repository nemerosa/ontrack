import {useEffect, useState} from "react";
import {Space, Typography} from "antd";
import {gql} from "graphql-request";
import Image from "next/image";
import {legacyPredefinedPromotionLevelImageUri} from "@components/common/Links";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import LegacyImage from "@components/common/LegacyImage";

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
                        <LegacyImage
                            href={legacyPredefinedPromotionLevelImageUri(ppl)}
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