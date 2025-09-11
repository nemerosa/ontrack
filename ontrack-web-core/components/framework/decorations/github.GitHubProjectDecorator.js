import {Tooltip, Typography} from "antd";
import {FaGithub} from "react-icons/fa";

export default function GitHubProjectDecorator({decoration}) {
    return (
        <Tooltip title={`GitHub repository: ${decoration.data.displayText}`}>
            <Typography.Link href={decoration.data.url}>
                <FaGithub/>
            </Typography.Link>
        </Tooltip>
    )
}