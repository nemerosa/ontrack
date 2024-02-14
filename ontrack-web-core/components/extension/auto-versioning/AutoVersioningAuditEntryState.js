import {Space, Tooltip, Typography} from "antd";
import {FaCheck, FaClock, FaRegClock, FaTimes, FaTimesCircle} from "react-icons/fa";

export default function AutoVersioningAuditEntryState({entry}) {
    return (
        <>
            {
                entry.mostRecentState.state === 'ERROR' &&
                <Typography.Text type="danger">
                    <Space>
                        <FaTimes/>
                        Error
                    </Space>
                </Typography.Text>
            }
            {
                entry.mostRecentState.state === 'PROCESSING_ABORTED' &&
                <Typography.Text type="warning">
                    <Space>
                        <FaRegClock/>
                        Processing aborted
                    </Space>
                </Typography.Text>
            }
            {
                entry.mostRecentState.state === 'PR_TIMEOUT' &&
                <Typography.Text type="danger">
                    <Tooltip title="The PR was created but its checks timed out before it could be merged.">
                        <Space>
                            <FaTimesCircle/>
                            PR timed out
                        </Space>
                    </Tooltip>
                </Typography.Text>
            }
            {
                entry.mostRecentState.state === 'PR_PROCESSED' &&
                <Typography.Text type="success">
                    <Space>
                        <FaCheck/>
                        PR processed
                    </Space>
                </Typography.Text>
            }
            {
                entry.mostRecentState.state === 'PR_MERGED' &&
                <Typography.Text type="success">
                    <Tooltip title="The PR has merged by Ontrack">
                        <Space>
                            <FaCheck/>
                            PR merged
                        </Space>
                    </Tooltip>
                </Typography.Text>
            }
            {
                entry.mostRecentState.state === 'PR_CREATED' &&
                <Typography.Text type="success">
                    <Tooltip title="The PR was created by Ontrack">
                        <Space>
                            <FaCheck/>
                            PR created
                        </Space>
                    </Tooltip>
                </Typography.Text>
            }
            {
                entry.mostRecentState.state === 'PR_APPROVED' &&
                <Typography.Text type="success">
                    <Tooltip title="The PR has created and approved by Ontrack">
                        <Space>
                            <FaCheck/>
                            PR approved
                        </Space>
                    </Tooltip>
                </Typography.Text>
            }
        </>
    )
}