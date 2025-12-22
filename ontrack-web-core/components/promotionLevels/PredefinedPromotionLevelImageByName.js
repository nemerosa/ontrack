import {Space, Typography} from "antd";
import {gql} from "graphql-request";
import LoadingInline from "@components/common/LoadingInline";
import PredefinedPromotionLevelImage from "@components/core/config/PredefinedPromotionLevelImage";
import {useQuery} from "@components/services/GraphQL";
import GeneratedIcon from "@components/common/icons/GeneratedIcon";

export default function PredefinedPromotionLevelImageByName({name, displayName = true, size = 24, generateIfMissing = false}) {

    const {data: predefinedPromotionLevel, loading} = useQuery(
        gql`
            query PredefinedPromotionLevel($name: String!) {
                predefinedPromotionLevelByName(name: $name) {
                    id
                    name
                    isImage
                }
            }
        `,
        {
            variables: {name},
            deps: [name],
            dataFn: data => data.predefinedPromotionLevelByName,
        }
    )

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
            {
                !predefinedPromotionLevel && generateIfMissing &&
                <Space>
                    <GeneratedIcon
                        name={name}
                        colorIndex={1}
                        size={size}
                    />
                    {displayName && <Typography.Text>{name}</Typography.Text>}
                </Space>
            }
        </LoadingInline>
    )
}