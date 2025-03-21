import {Space, Typography} from "antd";
import {FaStar} from "react-icons/fa";

export default function EnvironmentCurrentBuild({slot, build}) {
    return (
        <>
            {
                slot.lastDeployedPipeline && slot.lastDeployedPipeline.build.id === build.id &&
                <Space
                    style={{
                        backgroundColor: "lightyellow",
                        padding: '0.5em',
                        borderRadius: '0.5em',
                    }}
                >
                    <FaStar color="green"/>
                    <Typography.Text>Current build</Typography.Text>
                </Space>
            }
        </>
    )
}