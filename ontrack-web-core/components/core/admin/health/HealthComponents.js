import {List} from "antd";
import HealthIndicator from "@components/core/admin/health/HealthIndicator";

export default function HealthComponents({health}) {
    return (
        <>
            <List>
                <List.Item>
                    <List.Item.Meta
                        avatar={<HealthIndicator status={health.status}/>}
                        title="Global health"
                    />
                </List.Item>
                {
                    Object.keys(health.components).map(name => {
                        const component = health.components[name]
                        return (
                            <List.Item key={name}>
                                <List.Item.Meta
                                    avatar={<HealthIndicator status={component.status}/>}
                                    title={name}
                                    description={JSON.stringify(component.details, null, 2)}
                                />
                            </List.Item>
                        )
                    })
                }
            </List>
        </>
    )
}