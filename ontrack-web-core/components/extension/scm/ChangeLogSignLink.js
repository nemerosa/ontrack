import {Space, Typography} from "antd";
import {FaEquals, FaMinus, FaNotEqual, FaPlus} from "react-icons/fa";
import Link from "next/link";
import {scmChangeLogUri} from "@components/common/Links";

export default function ChangeLogSignLink({from, to}) {
    return (
        <>
            {
                from && to && from.id === to.id &&
                <Space>
                    <FaEquals/>
                    <Typography.Text disabled>
                        No change
                    </Typography.Text>
                </Space>
            }
            {
                !from && to &&
                <Space>
                    <FaPlus color="green"/>
                    <Typography.Text disabled>
                        Added
                    </Typography.Text>
                </Space>
            }
            {
                from && !to &&
                <Space>
                    <FaMinus color="red"/>
                    <Typography.Text disabled>
                        Removed
                    </Typography.Text>
                </Space>
            }
            {
                from && to && from.id !== to.id &&
                <Space>
                    <FaNotEqual color="orange"/>
                    <Link href={scmChangeLogUri(from.branch.scmBranchInfo.type, from.id, to.id)}>
                        <Typography.Link>
                            Change log
                        </Typography.Link>
                    </Link>
                </Space>
            }
        </>
    )
}