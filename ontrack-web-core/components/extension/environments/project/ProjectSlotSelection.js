import {Card, Empty} from "antd";
import {EventsContext} from "@components/common/EventsContext";
import {useContext, useEffect, useState} from "react";
import SlotCard from "@components/extension/environments/SlotCard";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {gqlSlotData, gqlSlotPipelineData} from "@components/extension/environments/EnvironmentGraphQL";
import EnvironmentTitle from "@components/extension/environments/EnvironmentTitle";

export default function ProjectSlotSelection() {
    const eventsContext = useContext(EventsContext)

    const client = useGraphQLClient()

    const [slotId, setSlotId] = useState()
    const [slot, setSlot] = useState()

    useEffect(() => {
        if (client) {
            if (slotId) {
                client.request(
                    gql`
                        ${gqlSlotData}
                        ${gqlSlotPipelineData}
                        query LoadSlot($id: String!) {
                            slotById(id: $id) {
                                ...SlotData
                                currentPipeline {
                                    ...SlotPipelineData
                                }
                                lastDeployedPipeline {
                                    ...SlotPipelineData
                                }
                            }
                        }
                    `,
                    {id: slotId}
                ).then(data => {
                    setSlot(data.slotById)
                })
            } else {
                setSlot(undefined)
            }
        }
    }, [client, slotId])

    eventsContext.subscribeToEvent("slot.selected", ({id}) => {
        setSlotId(id)
    })

    return (
        <>
            {
                !slot &&
                <Card
                    style={{border: 'solid 1px lightgray', height: '100%'}}
                >
                    <Empty description="Select a slot in the graph to get more details."/>
                </Card>
            }
            {
                slot &&
                <SlotCard
                    slot={slot}
                    showEligible={false}
                    title={
                        <EnvironmentTitle environment={slot.environment} tags={false}/>
                    }
                />
            }
        </>
    )
}