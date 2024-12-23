import {Button, Card, Empty, Space} from "antd";
import {useContext, useState} from "react";
import {EventsContext} from "@components/common/EventsContext";
import {FaAngleDoubleDown, FaAngleDoubleRight, FaPlay, FaThumbsUp} from "react-icons/fa";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";

export default function ProjectEnvironmentActions() {

    const eventsContext = useContext(EventsContext)

    const [buildId, setBuildId] = useState('')
    const [slotId, setSlotId] = useState('')

    eventsContext.subscribeToEvent("slot.selected", ({id}) => {
        setSlotId(id)
    })

    eventsContext.subscribeToEvent("build.selected", ({id}) => {
        setBuildId(id)
    })

    const client = useGraphQLClient()
    const [build, setBuild] = useState()
    const [slot, setSlot] = useState()

    return (
        <>
            <Card
                size="small"
                className="ot-block"
                bodyStyle={{
                    height: "100%",
                }}
            >
                {
                    (!buildId || !slotId) && <Empty
                        description="Select a build and a slot to perform some actions"
                    />
                }
                {
                    buildId && slotId && <div
                        style={{
                            display: "flex",
                            flexDirection: "column",
                            justifyContent: "space-between",
                            alignItems: "center",
                            height: "100%",
                        }}
                    >
                        {/* Header */}
                        <div style={{
                            display: "flex",
                            alignSelf: "flex-start",
                        }}>
                            <Space>
                                <FaAngleDoubleRight style={{
                                    alignSelf: "flex-start",
                                }}/>
                                With this build...
                            </Space>
                        </div>
                        {/* Body */}
                        <div style={{
                            display: "flex",
                            flexDirection: "column",
                            alignSelf: "center",
                            // border: "solid 1px green",
                            height: "100%",
                            width: "100%",
                            justifyContent: "center",
                            alignItems: "center",
                            gap: "1em",
                        }}>
                            {/* TODO Start a pipeline */}
                            <Button style={{
                                width: "100%",
                            }}>
                                <Space>
                                    <FaPlay/>
                                    Start a pipeline...
                                </Space>
                            </Button>
                            {/* TODO Start a deployment */}
                            <Button style={{
                                width: "100%",
                            }}>
                                <Space>
                                    <FaPlay/>
                                    Start deployment...
                                </Space>
                            </Button>
                            {/* TODO Finish a deployment */}
                            <Button style={{
                                width: "100%",
                            }}>
                                <Space>
                                    <FaThumbsUp/>
                                    Finish deployment...
                                </Space>
                            </Button>
                        </div>
                        {/* Footer */}
                        <div style={{
                            display: "flex",
                            alignSelf: "flex-end",
                        }}>
                            <Space>
                                ...into
                                <FaAngleDoubleDown style={{
                                    alignSelf: "flex-end",
                                }}/>
                            </Space>
                        </div>
                    </div>
                }
            </Card>
        </>
    )
}