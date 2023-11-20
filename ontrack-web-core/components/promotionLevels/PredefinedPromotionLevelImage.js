import {useEffect, useState} from "react";
import {Space, Typography} from "antd";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";
import Image from "next/image";
import {predefinedPromotionLevelImageUri} from "@components/common/Links";

export default function PredefinedPromotionLevelImage({name, displayName = true, size = 24}) {

    const [image, setImage] = useState('')

    useEffect(() => {
        graphQLCall(
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
                    <Image
                        src={predefinedPromotionLevelImageUri(ppl)}
                        alt={`Predefined promotion level ${name}`}
                        width={size}
                        height={size}
                    />
                )
            } else {
                setImage('')
            }
        })
    }, [name])

    return (
        <Space size={8}>
            {image}
            {displayName && <Typography.Text>{name}</Typography.Text>}
        </Space>
    )
}