import {Space, Typography} from "antd";
import {FaSearch} from "react-icons/fa";
import {useRouter} from "next/router";
import SelectBranch from "@components/branches/SelectBranch";

export default function JumpToBranch({projectName}) {

    const router = useRouter()

    const jumpToBranch = async (branchId) => {
        await router.push(`/branch/${branchId}`)
    }

    return (
        <>
            <Space>
                <FaSearch/>
                <Typography.Text>Branch</Typography.Text>
                <SelectBranch
                    project={projectName}
                    placeholder="Jump to branch"
                    idAsValue={true}
                    onChange={jumpToBranch}
                />
            </Space>
        </>
    )
}