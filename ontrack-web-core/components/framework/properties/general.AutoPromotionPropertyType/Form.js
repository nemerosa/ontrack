import {Form, Input} from "antd";
import {prefixedFormName} from "@components/form/formUtils";
import SelectValidationStamp from "@components/validationStamps/SelectValidationStamp";
import {useEffect, useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import SelectPromotionLevel from "@components/promotionLevels/SelectPromotionLevel";

export default function PropertyForm({prefix, entity}) {

    const {entityType, entityId} = entity
    if (entityType !== 'PROMOTION_LEVEL') throw new Error(`Expecting a promotion level, got ${entityType}`)

    const [promotionLevel, setPromotionLevel] = useState()
    const client = useGraphQLClient()

    useEffect(() => {
        if (client) {
            client.request(
                gql`
                    query GetPromotionLevel(
                        $id: Int!,
                    ) {
                        promotionLevel(id: $id) {
                            branch {
                                id
                            }
                        }
                    }
                `,
                {
                    id: entityId
                }
            ).then(data => {
                setPromotionLevel(data.promotionLevel)
            })
        }
    }, [client])

    return (
        <>
            {
                promotionLevel &&
                <Form.Item
                    label="Validation stamps"
                    extra="List of validation stamps which trigger this promotion"
                    name={prefixedFormName(prefix, 'validationStamps')}
                >
                    <SelectValidationStamp branch={promotionLevel.branch} multiple={true} useName={false}/>
                </Form.Item>
            }
            <Form.Item
                label="Including"
                extra="Regular expression to include validation stamps by name"
                name={prefixedFormName(prefix, 'include')}
            >
                <Input/>
            </Form.Item>
            <Form.Item
                label="Excluding"
                extra="Regular expression to exclude validation stamps by name"
                name={prefixedFormName(prefix, 'exclude')}
            >
                <Input/>
            </Form.Item>
            {
                promotionLevel &&
                <Form.Item
                    label="Promotion levels"
                    extra="List of promotion levels which trigger this promotion"
                    name={prefixedFormName(prefix, 'promotionLevels')}
                >
                    <SelectPromotionLevel branch={promotionLevel.branch} multiple={true} useName={false}/>
                </Form.Item>
            }
        </>
    )
}