import {Typography} from "antd";
import {FaCodeBranch} from "react-icons/fa";

export default function GitPullRequestBranchDecorator({decoration}) {
    return (
        <Typography.Text>
            <FaCodeBranch/>
        </Typography.Text>
    )
}