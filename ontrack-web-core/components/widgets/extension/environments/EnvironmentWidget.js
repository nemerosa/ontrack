import {useContext, useEffect, useState} from "react";
import {DashboardWidgetCellContext} from "@components/dashboards/DashboardWidgetCellContextProvider";
import {Empty, Space, Tag, Typography} from "antd";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import LoadingContainer from "@components/common/LoadingContainer";
import {gql} from "graphql-request";
import {gqlSlotPipelineData} from "@components/extension/environments/EnvironmentGraphQL";
import EnvironmentOrder from "@components/extension/environments/EnvironmentOrder";
import SlotCard from "@components/extension/environments/SlotCard";

export default function EnvironmentWidget({name = "", projects = []}) {

    const client = useGraphQLClient()
    const [loading, setLoading] = useState(true)
    const [environment, setEnvironment] = useState()
    const {setTitle} = useContext(DashboardWidgetCellContext)

    useEffect(() => {
        if (client && name) {
            setTitle(`${name} environment`)
            setLoading(true)
            client.request(
                gql`
                    query EnvironmentWidget($name: String!, $projects: [String!]) {
                        environmentByName(name: $name) {
                            id
                            name
                            order
                            description
                            tags
                            slots(projects: $projects) {
                                id
                                project {
                                    id
                                    name
                                }
                                qualifier
                                currentPipeline {
                                    ...SlotPipelineData
                                }
                                lastDeployedPipeline {
                                    ...SlotPipelineData
                                }
                            }
                        }
                    }
                    ${gqlSlotPipelineData}
                `,
                {
                    name,
                    projects,
                }
            ).then(data => {
                setEnvironment(data.environmentByName)
                setTitle(<Space>
                    <Typography.Text>{name} environment</Typography.Text>
                    {
                        data.environmentByName.tags.map((tag, index) =>
                            <Tag key={index}>{tag}</Tag>
                        )
                    }
                    <EnvironmentOrder order={data.environmentByName.order}/>
                </Space>)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, name])

    return (
        <>
            {
                !name && <Empty description="Environment name has not been configured."/>
            }
            {
                name && <LoadingContainer loading={loading}>
                    {
                        environment && <Space direction="vertical">
                            {environment.slots.map(slot => (<>
                                <SlotCard key={slot.id} slot={slot} showLastDeployed={true}/>
                            </>))
                            }
                        </Space>
                    }
                    {
                        !environment && <Empty description={`Environment ${name} could not be found.`}/>
                    }
                </LoadingContainer>
            }
        </>
    )

}