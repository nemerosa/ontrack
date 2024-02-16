import {Typography} from "antd";
import {FaGit} from "react-icons/fa";

export default function BasicGitProjectDecorator({decoration}) {
    return (
        <Typography.Text>
            <FaGit/>
        </Typography.Text>
    )
}