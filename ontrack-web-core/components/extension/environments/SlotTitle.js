import {Space, Typography} from "antd";
import ProjectLink from "@components/projects/ProjectLink";

export default function SlotTitle({slot}) {
    return (
        <>
            <Space>
                <ProjectLink project={slot.project}/>
                {
                    slot.qualifier &&
                    <Typography.Text>[{slot.qualifier}]</Typography.Text>
                }
            </Space>
        </>
    )
}