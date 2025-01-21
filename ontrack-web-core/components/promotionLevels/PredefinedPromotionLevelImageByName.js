import {useEffect, useState} from "react";
import {Space, Typography} from "antd";
import {gql} from "graphql-request";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import LoadingInline from "@components/common/LoadingInline";
import PredefinedPromotionLevelImage from "@components/core/config/PredefinedPromotionLevelImage";

export default function PredefinedPromotionLevelImageByName({name, displayName = true, size = 24}) {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(true)
    const [predefinedPromotionLevel, setPredefinedPromotionLevel] = useState()
    useEffect(() => {
        if (client) {
            setLoading(true)
            client.request(
                gql`
                    query PredefinedPromotionLevel($name: String!) {
                        predefinedPromotionLevelByName(name: $name) {
                            id
                            name
                            isImage
                        }
                    }
                `, {name}
            ).then(data => {
                setPredefinedPromotionLevel(data.predefinedPromotionLevelByName)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, name])

    return (
        <LoadingInline loading={loading} text="">
            {
                predefinedPromotionLevel &&
                <Space size={8}>
                    <PredefinedPromotionLevelImage
                        predefinedPromotionLevel={predefinedPromotionLevel}
                        title={`Predefined promotion level ${predefinedPromotionLevel.name}`}
                        size={size}
                    />
                    {displayName && <Typography.Text>{predefinedPromotionLevel.name}</Typography.Text>}
                </Space>
            }
        </LoadingInline>
    )
}