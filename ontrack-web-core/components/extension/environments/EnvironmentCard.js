import {Card, Flex, Space} from "antd";
import EnvironmentTitle from "@components/extension/environments/EnvironmentTitle";
import SlotTitle from "@components/extension/environments/SlotTitle";
import SlotLink from "@components/extension/environments/SlotLink";

export default function EnvironmentCard({environment, showSlots = true}) {
    return (
        <>
            <Card
                style={{
                    height: '100%',
                }}
                headStyle={{
                    background: 'linear-gradient(to right, #f1f1f1, #fafafa)'
                }}
                size="small"
                data-testid={`environment-${environment.id}`}
                title={
                    <EnvironmentTitle environment={environment}/>
                }
            >
                {
                    showSlots && <Space>
                        {
                            environment.slots.map(slot => (
                                <Card
                                    key={slot.id}
                                    style={{height: '100%'}}
                                    size="small"
                                    bodyStyle={{
                                        background: 'linear-gradient(to right, #f1f1f1, #fafafa)'
                                    }}
                                    hoverable={true}
                                >
                                    <Flex justify="space-between" align="center" gap={16}>
                                        <SlotTitle
                                            slot={slot}
                                            showLastDeployed={true}
                                        />
                                        <SlotLink
                                            slot={slot}
                                            text="Slot"
                                        />
                                    </Flex>
                                </Card>
                            ))
                        }
                    </Space>
                }
            </Card>
        </>
    )
}