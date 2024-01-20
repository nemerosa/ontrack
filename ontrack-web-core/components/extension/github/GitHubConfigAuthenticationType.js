import {Space, Tooltip} from "antd";
import {FaExclamationTriangle, FaGithub} from "react-icons/fa";

export default function GitHubConfigAuthenticationType({authenticationType}) {
    return (
        <>
            {
                authenticationType === 'ANONYMOUS' &&
                <Tooltip title="No credentials are provided for the authentication.">
                    Anonymous
                </Tooltip>
            }
            {
                authenticationType === 'PASSWORD' &&
                <Tooltip title="Username/password authentication. This should be replaced by using a token or a GitHub app.">
                    <Space>
                        <FaExclamationTriangle/>
                        Password
                    </Space>
                </Tooltip>
            }
            {
                authenticationType === 'USER_TOKEN' &&
                <Tooltip title="Username/token authentication">
                    User token
                </Tooltip>
            }
            {
                authenticationType === 'TOKEN' &&
                <Tooltip title="Token authentication">
                    Token
                </Tooltip>
            }
            {
                authenticationType === 'APP' &&
                <Tooltip title="GitHub app authentication">
                    <Space>
                        <FaGithub/>
                        Application
                    </Space>
                </Tooltip>
            }
        </>
    )
}