import {Space, Tooltip, Typography} from "antd";
import {
    FaCheck,
    FaClock, FaCodeBranch,
    FaCog, FaFile, FaPause,
    FaPlay,
    FaRegClock,
    FaThumbsUp,
    FaTimes,
    FaTimesCircle,
    FaWindowClose
} from "react-icons/fa";

export default function AutoVersioningAuditEntryState({status}) {
    return (
        <>
            {
                status.state === 'ERROR' &&
                <Typography.Text type="danger">
                    <Space>
                        <FaTimes/>
                        Error
                    </Space>
                </Typography.Text>
            }
            {
                status.state === 'PROCESSING_ABORTED' &&
                <Typography.Text type="warning">
                    <Space>
                        <FaRegClock/>
                        Processing aborted
                    </Space>
                </Typography.Text>
            }
            {
                status.state === 'PR_TIMEOUT' &&
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
                status.state === 'PR_PROCESSED' &&
                <Typography.Text type="success">
                    <Space>
                        <FaCheck/>
                        PR processed
                    </Space>
                </Typography.Text>
            }
            {
                status.state === 'PR_MERGED' &&
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
                status.state === 'PR_CREATED' &&
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
                status.state === 'PR_APPROVED' &&
                <Typography.Text type="success">
                    <Tooltip title="The PR has created and approved by Ontrack">
                        <Space>
                            <FaCheck/>
                            PR approved
                        </Space>
                    </Tooltip>
                </Typography.Text>
            }
            {
                status.state === 'CREATED' &&
                <Typography.Text type="secondary">
                    <Tooltip title="Auto versioning request registered.">
                        <Space>
                            <FaPlay/>
                            Created
                        </Space>
                    </Tooltip>
                </Typography.Text>
            }
            {
                status.state === 'RECEIVED' &&
                <Typography.Text type="secondary">
                    <Tooltip title="Auto versioning request dequeued.">
                        <Space>
                            <FaThumbsUp/>
                            Received
                        </Space>
                    </Tooltip>
                </Typography.Text>
            }
            {
                status.state === 'PROCESSING_START' &&
                <Typography.Text type="secondary">
                    <Tooltip title="Auto versioning processing started.">
                        <Space>
                            <FaCog/>
                            Processing started
                        </Space>
                    </Tooltip>
                </Typography.Text>
            }
            {
                status.state === 'PROCESSING_CANCELLED' &&
                <Typography.Text type="danger">
                    <Tooltip title="Auto versioning processing cancelled.">
                        <Space>
                            <FaWindowClose/>
                            Processing cancelled
                        </Space>
                    </Tooltip>
                </Typography.Text>
            }
            {
                status.state === 'PROCESSING_CREATING_BRANCH' &&
                <Typography.Text type="secondary">
                    <Tooltip title="Target branch being created.">
                        <Space>
                            <FaCodeBranch/>
                            Branch created
                        </Space>
                    </Tooltip>
                </Typography.Text>
            }
            {
                status.state === 'PROCESSING_UPDATING_FILE' &&
                <Typography.Text type="secondary">
                    <Tooltip title="Updating the files on the target branches">
                        <Space>
                            <FaFile/>
                            Updating files
                        </Space>
                    </Tooltip>
                </Typography.Text>
            }
            {
                status.state === 'POST_PROCESSING_START' &&
                <Typography.Text type="secondary">
                    <Tooltip title="Post processing starts">
                        <Space>
                            <FaCog/>
                            Post processing starts
                        </Space>
                    </Tooltip>
                </Typography.Text>
            }
            {
                status.state === 'POST_PROCESSING_END' &&
                <Typography.Text type="secondary">
                    <Tooltip title="Post processing ends">
                        <Space>
                            <FaPause/>
                            Post processing ends
                        </Space>
                    </Tooltip>
                </Typography.Text>
            }
            {
                status.state === 'PR_CREATING' &&
                <Typography.Text type="secondary">
                    <Tooltip title="PR is being created">
                        <Space>
                            <FaCodeBranch/>
                            Creating PR
                        </Space>
                    </Tooltip>
                </Typography.Text>
            }
        </>
    )
}