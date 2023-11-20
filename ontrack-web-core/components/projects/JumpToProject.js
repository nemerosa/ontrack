import {Space, Typography} from "antd";
import {FaSearch} from "react-icons/fa";
import SelectProject from "@components/projects/SelectProject";
import {useRouter} from "next/router";

export default function JumpToProject() {

    const router = useRouter()

    const jumpToProject = async (projectId) => {
        await router.push(`/project/${projectId}`)
    }

    return (
        <>
            <Space>
                <FaSearch/>
                <Typography.Text>Project</Typography.Text>
                <SelectProject
                    placeholder="Jump to project"
                    idAsValue={true}
                    onChange={jumpToProject}
                />
            </Space>
        </>
    )
}